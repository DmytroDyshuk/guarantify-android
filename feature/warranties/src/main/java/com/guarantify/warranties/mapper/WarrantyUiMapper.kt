package com.guarantify.warranties.mapper

import androidx.compose.ui.graphics.Color
import com.guarantify.domain.model.Warranty
import com.guarantify.util.date.DateFormatter
import com.guarantify.warranties.details.state.WarrantyDetailsUi
import com.guarantify.warranties.list.model.WarrantyListItemUi
import com.guarantify.warranties.model.WarrantyStatus
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class WarrantyUiMapper @Inject constructor(
    private val dateFormatter: DateFormatter
) {
    fun toListItem(w: Warranty): WarrantyListItemUi {
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

        return WarrantyListItemUi(
            id = w.id,
            title = w.productName,
            brand = w.brand,
            storeName = w.storeName,
            remainingDays = remainingDaysText,
            status = statusColor,
            formattedExpirationDate = "Valid until: ${dateFormatter.formatToFullText(w.expirationDate)}",
            formattedPurchaseDate = "Purchased: ${dateFormatter.formatToShortText(w.purchaseDate)}"
        )
    }

    fun toDetails(w: Warranty): WarrantyDetailsUi {
        val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), w.expirationDate)

        val status = when {
            daysRemaining < 0 -> WarrantyStatus.EXPIRED
            daysRemaining < 30 -> WarrantyStatus.CRITICAL
            daysRemaining < 100 -> WarrantyStatus.WARNING
            else -> WarrantyStatus.LONG_TERM
        }

        return WarrantyDetailsUi(
            productName = w.productName,
            brand = w.brand,
            store = w.storeName,
            purchaseDate = dateFormatter.formatToShortText(w.purchaseDate),
            expirationDate = dateFormatter.formatToShortText(w.expirationDate),
            amount = null, //TODO: add amount to Warranty domain
            currency = w.currency,
            photoUrl = w.photoUrl,
            notest = w.notes,
            status = status,
            remainingDays = daysRemaining.toInt()
        )

    }
}