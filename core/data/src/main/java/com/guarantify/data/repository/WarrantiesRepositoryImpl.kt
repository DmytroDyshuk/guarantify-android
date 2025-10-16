package com.guarantify.data.repository

import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.di.IoDispatcher
import com.guarantify.data.network.firebase.FirebaseWarrantyDataSource
import com.guarantify.domain.model.Warranty
import com.guarantify.domain.repository.WarrantiesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class WarrantiesRepositoryImpl @Inject constructor(
    private val firebaseWarrantyDataSource: FirebaseWarrantyDataSource,
    private val warrantyDao: WarrantyDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WarrantiesRepository {
    override suspend fun createWarranty(warranty: Warranty) {
        TODO("Not yet implemented")
    }

    override fun getWarranties(userId: String): Flow<List<Warranty>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWarranty(warrantyId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun syncWarranties() {
        TODO("Not yet implemented")
    }
}