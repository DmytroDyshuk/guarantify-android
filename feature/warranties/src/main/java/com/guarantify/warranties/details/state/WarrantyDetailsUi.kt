package com.guarantify.warranties.details.state

import com.guarantify.warranties.model.WarrantyStatus

data class WarrantyDetailsUi(
    val productName: String,
    val brand: String? = null,
    val store: String? = null,
    val purchaseDate: String,
    val expirationDate: String,
    val warrantyExpirationProgress: Float,
    val priceText: String? = null,
    val photoUrl: String? = null,
    val serialNumber: String? = null,
    val notes: String? = null,
    val status: WarrantyStatus,
    val remainingDays: Int
)
