package com.guarantify.warranties.create.viewmodel

import androidx.compose.material3.DatePickerDefaults.dateFormatter
import androidx.lifecycle.ViewModel
import com.guarantify.domain.repository.WarrantiesRepository
import com.guarantify.ui.di.AppDateFormatProvider
import com.guarantify.warranties.create.state.CreateWarrantyEvent
import com.guarantify.warranties.create.state.CreateWarrantyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@HiltViewModel
class CreateWarrantyViewModel @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository,
    private val dateFormatProvider: AppDateFormatProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateWarrantyUiState())
    val uiState: StateFlow<CreateWarrantyUiState> = _uiState.asStateFlow()

    fun onEvent(event: CreateWarrantyEvent) {
        when (event) {
            is CreateWarrantyEvent.ProductNameChanged -> {
                _uiState.update { it.copy(productName = event.value) }
            }
            is CreateWarrantyEvent.BrandChanged -> {
                _uiState.update { it.copy(brand = event.value) }
            }
            is CreateWarrantyEvent.StoreNameChanged -> {
                _uiState.update { it.copy(storeName = event.value) }
            }
            is CreateWarrantyEvent.NotesChanged -> {
                _uiState.update { it.copy(notes = event.value) }
            }
            is CreateWarrantyEvent.PriceChanged -> {
                _uiState.update { it.copy(price = event.value) }
            }
            is CreateWarrantyEvent.CurrencyChanged -> {
                _uiState.update { it.copy(selectedCurrency = event.value) }
            }
            is CreateWarrantyEvent.PurchaseDateSelected -> {
                _uiState.update {
                    it.copy(
                        purchaseDateMillis = event.date,
                        purchaseDateText = event.date?.toDateText() ?: ""
                    )
                }
            }
            is CreateWarrantyEvent.ExpirationDateSelected -> {
                _uiState.update {
                    it.copy(
                        expirationDateMillis = event.date,
                        expirationDateText = event.date?.toDateText() ?: ""
                    )
                }
            }
            is CreateWarrantyEvent.SaveClicked -> {
                //TODO: implement save()
            }
        }
    }

    private fun Long.toDateText(): String {
        val localDate = Instant.ofEpochMilli(this)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        return localDate.format(dateFormatProvider.shortDate)
    }

}