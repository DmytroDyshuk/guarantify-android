package com.guarantify.warranties.create.state

import java.time.LocalDate

data class CreateWarrantyUiState(
    val productName: String = "",
    val brand: String = "",
    val storeName: String = "",
    val price: String = "",
    val selectedCurrency: String = "USD",
    val purchaseDate: LocalDate? = null,
    val purchaseDateText: String = "",
    val expirationDate: LocalDate? = null,
    val expirationDateText: String = "",
    val notes: String = ""
)