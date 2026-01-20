package com.guarantify.warranties.create.state

data class CreateWarrantyErrors(
    val productNameError: String? = null,
    val storeNameError: String? = null,
    val purchaseDateError: String? = null,
    val expirationDateError: String? = null,
    val priceError: String? = null
)
