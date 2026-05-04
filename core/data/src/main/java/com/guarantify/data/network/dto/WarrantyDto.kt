package com.guarantify.data.network.dto

import androidx.annotation.Keep

@Keep
data class WarrantyDto(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val purchaseDate: String = "",
    val expirationDate: String = "",
    val storeName: String? = null,
    val brand: String? = null,
    val amount: Long? = null,
    val currency: String,
    val serialNumber: String? = null,
    val photoUrl: String? = null,
    val notes: String? = null,
    val updatedAt: Long = 0,
    val isDeleted: Boolean = false
)