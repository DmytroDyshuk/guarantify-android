package com.guarantify.warranties.list.model

import androidx.compose.ui.graphics.Color

data class WarrantyListItemUi(
    val id: String,
    val title: String,
    val brand: String?,
    val storeName: String?,
    val remainingDays: String,
    val status: Color,
    val formattedExpirationDate: String,
    val formattedPurchaseDate: String
)