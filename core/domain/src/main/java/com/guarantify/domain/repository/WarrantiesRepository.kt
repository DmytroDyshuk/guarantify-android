package com.guarantify.domain.repository

import com.guarantify.domain.model.Result
import com.guarantify.domain.model.Warranty
import kotlinx.coroutines.flow.Flow

interface WarrantiesRepository {
    suspend fun createOrUpdateWarranty(warranty: Warranty): Result<Unit>
    fun getWarranties(userId: String): Flow<List<Warranty>>
    suspend fun deleteWarranty(warrantyId: String): Result<Unit>
    suspend fun syncWarranties()
}