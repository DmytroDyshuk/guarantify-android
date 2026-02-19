package com.guarantify.data.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import com.google.firebase.FirebaseException
import com.guarantify.common.di.IoDispatcher
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.mapper.toDomain
import com.guarantify.data.mapper.toDto
import com.guarantify.data.mapper.toEntityWithGeneratedIdIfNeeded
import com.guarantify.data.network.firebase.firestore.FirebaseWarrantyDataSource
import com.guarantify.domain.model.SyncStatus
import com.guarantify.domain.model.Warranty
import com.guarantify.domain.repository.WarrantiesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

const val WARRANTIES_REPO = "WarrantiesRepo"

class WarrantiesRepositoryImpl @Inject constructor(
    private val firebaseWarrantyDataSource: FirebaseWarrantyDataSource,
    private val warrantyDao: WarrantyDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WarrantiesRepository {
    override val latestWarranties: Flow<List<Warranty>>
        get() = warrantyDao.getAllWarranties()
            .map { entities -> entities.map { it.toDomain() } }
            .catch { e ->
                Log.e(WARRANTIES_REPO, "${e.message}")
                emit(emptyList())
            }

    override suspend fun createOrUpdateWarranty(warranty: Warranty): Result<Unit> =
        withContext(ioDispatcher) {
            val warrantyEntity = warranty.toEntityWithGeneratedIdIfNeeded()

            val localResult = try {
                warrantyDao.createOrUpdateWarranty(warrantyEntity)
                Result.Success(Unit)
            } catch (e: SQLiteException) {
                Log.e(WARRANTIES_REPO, "Critical Database Error", e)
                return@withContext Result.Error(e)
            }

            try {
                firebaseWarrantyDataSource.createOrUpdateWarranty(warrantyEntity.toDto())
                warrantyDao.updateSyncStatus(warrantyEntity.id, syncStatus = SyncStatus.COMPLETED)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is IOException, is FirebaseException -> {
                        Log.e(WARRANTIES_REPO, "Sync failed, but data saved locally", e)
                    }

                    else -> {
                        Log.e(WARRANTIES_REPO, "Unexpected error during sync", e)
                    }
                }
            }

            localResult
        }

    override suspend fun getWarranty(warrantyId: String): Result<Warranty> =
        withContext(ioDispatcher) {
            try {
                val warranty = warrantyDao.getWarrantyById(warrantyId)?.toDomain()
                    ?: return@withContext Result.Error(Exception("Warranty not found"))
                Result.Success(warranty)
            } catch (e: CancellationException) {
                throw e
            } catch (e: SQLiteException) {
                Result.Error(e)
            }
        }

    override suspend fun deleteWarranty(warranty: Warranty) {
        withContext(ioDispatcher) {
            val warrantyEntity = warranty.toEntityWithGeneratedIdIfNeeded()
            warrantyDao.deleteWarranty(warrantyEntity)

            try {
                firebaseWarrantyDataSource.deleteWarranty(warranty.id)
            } catch (e: Exception) {
                Log.e(WARRANTIES_REPO, "Failed to delete warranty on firebase: ${warranty.id}", e)
            }
        }
    }

    override suspend fun syncWarranties() {
        TODO("Not yet implemented")
    }
}