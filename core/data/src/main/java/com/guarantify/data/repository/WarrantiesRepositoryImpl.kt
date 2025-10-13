package com.guarantify.data.repository

import com.guarantify.domain.model.Warranty
import com.guarantify.domain.repository.WarrantiesRepository
import kotlinx.coroutines.flow.Flow

class WarrantiesRepositoryImpl(

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