package com.guarantify.data.repository

import android.util.Log
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.di.IoDispatcher
import com.guarantify.data.mapper.toDto
import com.guarantify.data.mapper.toEntityWithGeneratedIdIfNeeded
import com.guarantify.data.network.firebase.FirebaseWarrantyDataSource
import com.guarantify.domain.model.Result
import com.guarantify.domain.model.Warranty
import com.guarantify.domain.repository.WarrantiesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WarrantiesRepositoryImpl @Inject constructor(
    private val firebaseWarrantyDataSource: FirebaseWarrantyDataSource,
    private val warrantyDao: WarrantyDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WarrantiesRepository {
    override suspend fun createOrUpdateWarranty(warranty: Warranty): Result<Unit> =
        withContext(ioDispatcher) {
            val warrantyEntity = warranty.toEntityWithGeneratedIdIfNeeded()
            warrantyDao.createOrUpdateWarranty(warrantyEntity)

            return@withContext try {
                firebaseWarrantyDataSource.createOrUpdateWarranty(warrantyEntity.toDto())
                warrantyDao.createOrUpdateWarranty(warrantyEntity.copy(isSynced = true))
                Result.Success(Unit)
            } catch (e: Exception) {
                Log.e("WarrantiesRepo", "Sync failed for warranty: ${warranty.id}", e)
                Result.Error(errorMessage = e.message)
            }
        }

    override fun getWarranties(userId: String): Flow<List<Warranty>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWarranty(warrantyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun syncWarranties() {
        TODO("Not yet implemented")
    }
}