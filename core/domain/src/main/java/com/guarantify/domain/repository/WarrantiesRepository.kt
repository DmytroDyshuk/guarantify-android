package com.guarantify.domain.repository

import com.guarantify.common.result.Result
import com.guarantify.domain.model.Warranty
import kotlinx.coroutines.flow.Flow

interface WarrantiesRepository {
    val latestWarranties: Flow<List<Warranty>>
    suspend fun createOrUpdateWarranty(warranty: Warranty): Result<Unit>
    suspend fun getWarranty(warrantyId: String): Result<Warranty>
    suspend fun deleteWarranty(warranty: Warranty)
}