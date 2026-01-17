package com.guarantify.warranties.create.state

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
    val notes: String = "",
    val photoUri: String? = null,
    val photoUploadError: String? = null,
    val errors: CreateWarrantyErrors = CreateWarrantyErrors(),
    val attemptedSubmit: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val saveSuccess: Boolean = false
)