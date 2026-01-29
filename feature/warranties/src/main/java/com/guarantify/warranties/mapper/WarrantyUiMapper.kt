package com.guarantify.warranties.mapper

import androidx.compose.ui.graphics.Color
import com.guarantify.domain.model.Warranty
import com.guarantify.util.date.DateFormatter
import com.guarantify.warranties.list.model.WarrantyUiModel
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class WarrantyUiMapper @Inject constructor(
    private val dateFormatter: DateFormatter
) {
    fun map(w: Warranty): WarrantyUiModel {
        val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), w.expirationDate)

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
            id = w.id,
            title = w.productName,
            brand = w.brand,
            storeName = w.storeName,
            remainingDays = remainingDaysText,
            status = statusColor,
            formattedExpirationDate = "Valid until: ${dateFormatter.formatToShortText(w.expirationDate)}",
            formattedPurchaseDate = "Purchased: ${dateFormatter.formatToShortText(w.purchaseDate)}"
        )
    }
}