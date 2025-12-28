package com.guarantify.warranties.create.state

data class CreateWarrantyUiState(
    val productName: String = "",
    val brand: String = "",
    val storeName: String = "",
    val price: String = "",
    val selectedCurrency: String = "USD",
    val purchaseDate: Long? = null,
    val expirationDate: Long? = null,
    val notes: String = ""
)