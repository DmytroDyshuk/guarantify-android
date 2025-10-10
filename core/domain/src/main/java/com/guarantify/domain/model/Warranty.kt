package com.guarantify.domain.model

import java.time.LocalDate

data class Warranty(
    val id: String,
    val userId: String,
    val title: String,
    val purchaseDate: LocalDate,
    val warrantyPeriod: Int,
    val expirationDate: LocalDate,
    val shopName: String,
    val photoUrl: String?,
    val notes: String?,
    val synced: Boolean = false
)