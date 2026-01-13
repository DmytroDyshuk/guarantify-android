package com.guarantify.warranties.create.state

import android.net.Uri
import java.time.LocalDate

sealed interface CreateWarrantyEvent {
    data class ProductNameChanged(val value: String) : CreateWarrantyEvent
    data class BrandChanged(val value: String) : CreateWarrantyEvent
    data class StoreNameChanged(val value: String) : CreateWarrantyEvent
    data class PriceChanged(val value: String) : CreateWarrantyEvent
    data class CurrencyChanged(val value: String) : CreateWarrantyEvent
    data class PurchaseDateSelected(val date: Long?) : CreateWarrantyEvent
    data class ExpirationDateSelected(val date: Long?) : CreateWarrantyEvent
    data class NotesChanged(val value: String) : CreateWarrantyEvent
    data class PhotoPicked(val value: Uri) : CreateWarrantyEvent
    object PhotoRemoved : CreateWarrantyEvent
    object SaveClicked : CreateWarrantyEvent
}