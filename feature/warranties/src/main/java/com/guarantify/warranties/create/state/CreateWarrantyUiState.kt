package com.guarantify.warranties.create.state

import java.time.LocalDate

data class CreateWarrantyUiState(
    val productName: String = "",
    val brand: String = "",
    val storeName: String = "",
    val price: String = "",
    val selectedCurrency: String = "USD",
    val purchaseDateMillis: Long? = null,
    val purchaseDateText: String = "",
    val expirationDateMillis: Long? = null,
    val expirationDateText: String = "",
    val notes: String = ""
)