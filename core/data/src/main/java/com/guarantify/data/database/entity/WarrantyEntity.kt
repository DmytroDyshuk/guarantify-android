package com.guarantify.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val photoUrl: String? = null,
    val notes: String? = null,
    val updatedAt: Long = 0,
    val isSynced: Boolean = false
)