package com.guarantify.data.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import com.guarantify.common.di.IoDispatcher
import com.guarantify.common.result.Result
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.mapper.toDomain
import com.guarantify.data.mapper.toEntityWithGeneratedIdIfNeeded
import com.guarantify.data.network.firebase.firestore.FirestoreWarrantyDataSource
import com.guarantify.domain.model.SyncStatus
import com.guarantify.domain.model.Warranty
import com.guarantify.domain.repository.WarrantiesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

const val WARRANTIES_REPO = "WarrantiesRepository"

class WarrantiesRepositoryImpl @Inject constructor(
    private val firestoreWarrantyDataSource: FirestoreWarrantyDataSource,
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

    override suspend fun getWarranty(warrantyId: String): Result<Warranty> =
        withContext(ioDispatcher) {
            try {
                val warranty = warrantyDao.getWarrantyById(warrantyId)?.toDomain()
                    ?: return@withContext Result.Error(Exception("Warranty not found"))
                Result.Success(warranty)
            } catch (e: SQLiteException) {
                Result.Error(e)
            }
        }

    override suspend fun createOrUpdateWarranty(warranty: Warranty): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val warrantyEntity = warranty.toEntityWithGeneratedIdIfNeeded()
                    .copy(syncStatus = SyncStatus.PENDING)
                warrantyDao.createOrUpdateWarranty(warrantyEntity)
                Result.Success(Unit)
            } catch (e: SQLiteException) {
                Result.Error(e)
            }
        }

    override suspend fun deleteWarranty(warranty: Warranty) {
        withContext(ioDispatcher) {
            val now = System.currentTimeMillis()
            val warrantyEntity = warranty.toEntityWithGeneratedIdIfNeeded()

            warrantyDao.markWarrantyAsDeleted(
                id = warrantyEntity.id,
                updatedAt = now,
                syncStatus = SyncStatus.PENDING
            )
        }
    }

}