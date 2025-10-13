package com.guarantify.data.network.dto

import androidx.annotation.Keep
import java.time.LocalDate

@Keep
data class WarrantyDto(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val purchaseDate: String = "",
    val warrantyPeriod: Int = 0,
    val expirationDate: String = "",
    val shopName: String = "",
    val photoUrl: String? = null,
    val notes: String? = "",
    val updatedAt: Long = 0,
    val isSynced: Boolean = false
)