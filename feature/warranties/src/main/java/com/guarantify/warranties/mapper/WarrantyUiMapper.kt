package com.guarantify.warranties.mapper

import androidx.compose.ui.graphics.Color
import com.guarantify.domain.model.Warranty
import com.guarantify.warranties.model.WarrantyUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun Warranty.toWarrantyUiModel(dateFormatter: DateTimeFormatter): WarrantyUiModel {
    val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), this.expirationDate)

    val statusColor = when {
        daysRemaining < 0 -> Color.Gray
        daysRemaining < 25 -> Color.Red
        daysRemaining < 50 -> Color(0xFFFF9800)
        daysRemaining < 100 -> Color.Yellow
        else -> Color.Green
    }

    val remainingDaysText = when {
        daysRemaining < 0 -> "Expired"
        daysRemaining == 0L -> "Expires today"
        else -> "$daysRemaining days remaining"
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