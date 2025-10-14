package com.guarantify.domain.model

import java.time.LocalDate

data class Warranty(
    val localId: Int,
    val remoteId: String?,
    val userId: String,
    val title: String,
    val purchaseDate: LocalDate,
    val warrantyPeriod: Int,
    val expirationDate: LocalDate,
    val shopName: String? = null,
    val photoUrl: String? = null,
    val notes: String? = null,
    val isSynced: Boolean = false
)