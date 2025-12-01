package com.guarantify.warranties.list.mapper

import androidx.compose.ui.graphics.Color
import com.guarantify.domain.model.Warranty
import com.guarantify.warranties.list.model.WarrantyUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun Warranty.toWarrantyUiModel(dateFormatter: DateTimeFormatter): WarrantyUiModel {
    val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), this.expirationDate)

    val statusColor = when {
        daysRemaining < 0 -> Color.Gray
        daysRemaining < 25 -> Color.Red
        daysRemaining < 100 -> Color(0xFFFF9800)
        else -> Color.Green
    }

    val remainingDaysText = when {
        daysRemaining < 0 -> "Expired"
        daysRemaining == 0L -> "Expires today"
        daysRemaining > 365 -> "Valid"
        else -> "$daysRemaining days left"
    }

    return WarrantyUiModel(
        id = this.id,
        title = this.title,
        brand = this.brand,
        storeName = this.storeName,
        remainingDays = remainingDaysText,
        status = statusColor,
        formattedExpirationDate = "Valid until: ${this.expirationDate.format(dateFormatter)}",
        formattedPurchaseDate = "Purchased: ${this.purchaseDate.format(dateFormatter)}"
    )
}