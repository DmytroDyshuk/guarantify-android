package com.guarantify.data.repository

import android.util.Log
import com.guarantify.common.di.IoDispatcher
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.mapper.toDomain
import com.guarantify.data.mapper.toDto
import com.guarantify.data.mapper.toEntityWithGeneratedIdIfNeeded
import com.guarantify.data.network.firebase.FirebaseWarrantyDataSource
import com.guarantify.domain.model.Result
import com.guarantify.domain.model.Warranty
import com.guarantify.domain.repository.WarrantiesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
            warrantyDao.createOrUpdateWarranty(warrantyEntity)

            return@withContext try {
                firebaseWarrantyDataSource.createOrUpdateWarranty(warrantyEntity.toDto())
                warrantyDao.createOrUpdateWarranty(warrantyEntity.copy(isSynced = true))
                Result.Success(Unit)
            } catch (e: Exception) {
                Log.e(WARRANTIES_REPO, "Sync failed for warranty: ${warranty.id}", e)
                Result.Error(errorMessage = e.message)
            }
        }

    override suspend fun getWarranty(warrantyId: String): Result<Warranty> =
        withContext(ioDispatcher) {
            try {
                Result.Success(warrantyDao.getWarrantyById(warrantyId).toDomain())
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Result.Error(e.message ?: "Warranty not found")
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