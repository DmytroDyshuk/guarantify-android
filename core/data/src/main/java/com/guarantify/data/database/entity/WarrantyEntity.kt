package com.guarantify.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warranties")
data class WarrantyEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int,
    val remoteId: String? = null,
    val userId: String,
    val title: String,
    val purchaseDate: String,
    val warrantyPeriod: Int,
    val expirationDate: String,
    val shopName: String? = null,
    val photoUrl: String? = null,
    val notes: String? = null,
    val updatedAt: Long = 0,
    val isSynced: Boolean = false
)