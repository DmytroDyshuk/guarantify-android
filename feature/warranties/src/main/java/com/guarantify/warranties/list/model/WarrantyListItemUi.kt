package com.guarantify.warranties.list.model

import com.guarantify.warranties.model.WarrantyStatus

data class WarrantyListItemUi(
    val id: String,
    val title: String,
    val brand: String?,
    val storeName: String?,
    val remainingDays: Int,
    val status: WarrantyStatus,
    val formattedExpirationDate: String,
    val formattedPurchaseDate: String
)