package com.guarantify.data.network.firebase.firestore

import com.guarantify.data.network.dto.WarrantyDto

interface FirestoreWarrantyDataSource {
    suspend fun createOrUpdateWarranty(warranty: WarrantyDto)
    suspend fun getAllWarranties(): List<WarrantyDto>
    suspend fun getUpdatedSince(timestamp: Long): List<WarrantyDto>
    suspend fun deleteWarranty(id: String)
}