package com.guarantify.domain.repository

import com.guarantify.domain.model.Warranty
import kotlinx.coroutines.flow.Flow

interface WarrantiesRepository {
    suspend fun createWarranty(warranty: Warranty)
    fun getWarranties(userId: String): Flow<List<Warranty>>
    suspend fun deleteWarranty(warrantyId: String)
    suspend fun syncWarranties()
}