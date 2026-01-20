package com.guarantify.data.network.firebase

import com.guarantify.data.network.dto.WarrantyDto

interface FirebaseWarrantyDataSource {
    suspend fun createOrUpdateWarranty(warranty: WarrantyDto)
    suspend fun getAllWarranties(): List<WarrantyDto>
    suspend fun getUpdatedSince(timestamp: Long): List<WarrantyDto>
    suspend fun deleteWarranty(id: String)
}