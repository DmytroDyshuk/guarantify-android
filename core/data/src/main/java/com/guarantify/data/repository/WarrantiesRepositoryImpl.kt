package com.guarantify.data.repository

import android.util.Log
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.di.IoDispatcher
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

const val WARRANTIES_REPO = "WarrantiesRepo"

class WarrantiesRepositoryImpl @Inject constructor(
    private val firebaseWarrantyDataSource: FirebaseWarrantyDataSource,
    private val warrantyDao: WarrantyDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WarrantiesRepository {
    override val latestWarranties: Flow<List<Warranty>> =
        warrantyDao.getAllWarranties()
            .map { entities -> entities.map { it.toDomain() } }
            .catch { e -> Log.e(WARRANTIES_REPO, "${e.message}") }

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

    override suspend fun deleteWarranty(warrantyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun syncWarranties() {
        TODO("Not yet implemented")
    }
}