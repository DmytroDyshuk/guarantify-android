package com.guarantify.data.network.dto

import androidx.annotation.Keep
import java.time.LocalDate

@Keep
data class WarrantyDto(
    val localId: Int = 0,
    val remoteId: String? = "",
    val userId: String = "",
    val title: String = "",
    val purchaseDate: String = "",
    val warrantyPeriod: Int = 0,
    val expirationDate: String = "",
    val shopName: String? = null,
    val photoUrl: String? = null,
    val notes: String? = null,
    val updatedAt: Long = 0,
    val isSynced: Boolean = false
)