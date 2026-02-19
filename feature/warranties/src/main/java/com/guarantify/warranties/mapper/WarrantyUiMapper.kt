package com.guarantify.warranties.mapper

import com.guarantify.domain.model.Warranty
import com.guarantify.util.date.DateFormatter
import com.guarantify.util.money.MoneyFormatter
import com.guarantify.warranties.details.state.WarrantyDetailsUi
import com.guarantify.warranties.extensions.toWarrantyStatus
import com.guarantify.warranties.list.model.WarrantyListItemUi
import com.guarantify.warranties.model.WarrantyStatus
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class WarrantyUiMapper @Inject constructor(
    private val dateFormatter: DateFormatter
) {

    private data class WarrantyComputed(
        val daysRemaining: Long,
        val status: WarrantyStatus,
        val warrantyProgress: Float
    )

    private fun computeWarranty(w: Warranty): WarrantyComputed {
        val today = LocalDate.now()
        val daysRemaining = ChronoUnit.DAYS.between(today, w.expirationDate)
        val totalDuration = ChronoUnit.DAYS.between(w.purchaseDate, w.expirationDate)

        val warrantyProgress = when {
            daysRemaining <= 0 -> 0f
            totalDuration <= 0 -> 0f
            else -> (daysRemaining.toFloat() / totalDuration.toFloat())
        }

        return WarrantyComputed(
            daysRemaining = daysRemaining,
            status = warrantyProgress.toWarrantyStatus(),
            warrantyProgress = warrantyProgress
        )
    }

    fun toListItem(w: Warranty): WarrantyListItemUi {
        val computed = computeWarranty(w)

        return WarrantyListItemUi(
            id = w.id,
            title = w.productName,
            brand = w.brand,
            storeName = w.storeName,
            remainingDays = computed.daysRemaining.toInt(),
            status = computed.status,
            formattedExpirationDate = dateFormatter.formatToFullText(w.expirationDate),
            formattedPurchaseDate = dateFormatter.formatToShortText(w.purchaseDate)
        )
    }

    fun toDetails(w: Warranty): WarrantyDetailsUi {
        val computed = computeWarranty(w)

        return WarrantyDetailsUi(
            productName = w.productName,
            brand = w.brand,
            store = w.storeName,
            purchaseDate = dateFormatter.formatToShortText(w.purchaseDate),
            expirationDate = dateFormatter.formatToShortText(w.expirationDate),
            warrantyExpirationProgress = computed.warrantyProgress,
            priceText = w.amount?.let { MoneyFormatter.minorUnitsToString(it, w.currency) },
            photoUrl = w.remotePhotoUrl ?: w.localPhotoUri,
            serialNumber = w.serialNumber,
            notes = w.notes,
            status = computed.status,
            remainingDays = computed.daysRemaining.toInt()
        )
    }

}