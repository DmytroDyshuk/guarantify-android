package com.guarantify.data.network.firebase.firestore

import com.guarantify.data.network.dto.WarrantyDto

interface FirestoreWarrantyDataSource {
    suspend fun createOrUpdateWarranty(warranty: WarrantyDto)
    suspend fun getAllWarranties(): List<WarrantyDto>
    suspend fun getWarrantiesUpdatedSince(timestamp: Long): List<WarrantyDto>
    suspend fun deleteWarranty(id: String)
    suspend fun pushChangesBatch(toUpload: List<WarrantyDto>, toDelete: List<String>)
}