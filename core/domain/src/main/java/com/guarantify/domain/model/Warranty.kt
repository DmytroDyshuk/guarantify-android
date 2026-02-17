package com.guarantify.domain.model

import java.time.LocalDate

data class Warranty(
    val id: String = "",
    val userId: String,
    val productName: String,
    val purchaseDate: LocalDate,
    val expirationDate: LocalDate,
    val storeName: String? = null,
    val brand: String? = null,
    val amount: Long? = null,
    val currency: String,
    val photoUrl: String? = null,
    val serialNumber: String? = null,
    val notes: String? = null,
    val updatedAt: Long = 0
)