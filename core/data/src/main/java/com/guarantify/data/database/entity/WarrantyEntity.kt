package com.guarantify.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guarantify.domain.model.sync.SyncStatus
import java.time.LocalDate

@Entity(tableName = "warranties")
data class WarrantyEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val purchaseDate: LocalDate,
    val expirationDate: LocalDate,
    val storeName: String? = null,
    val brand: String? = null,
    val amount: Long? = null,
    val serialNumber: String? = null,
    val currency: String = "USD",
    val localPhotoUri: String? = null,
    val remotePhotoUrl: String? = null,
    val notes: String? = null,
    val updatedAt: Long = 0,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val isDeleted: Boolean = false
)