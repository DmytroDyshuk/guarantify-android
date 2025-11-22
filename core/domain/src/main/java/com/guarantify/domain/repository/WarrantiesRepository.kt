package com.guarantify.domain.repository

import com.guarantify.domain.model.Result
import com.guarantify.domain.model.Warranty
import kotlinx.coroutines.flow.Flow

interface WarrantiesRepository {
    val latestWarranties: Flow<List<Warranty>>
    suspend fun createOrUpdateWarranty(warranty: Warranty): Result<Unit>
    suspend fun deleteWarranty(warranty: Warranty)
    suspend fun syncWarranties()
}