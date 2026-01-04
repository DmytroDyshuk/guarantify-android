package com.guarantify.warranties.create.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.guarantify.domain.repository.WarrantiesRepository
import com.guarantify.ui.di.AppDateFormatProvider
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

@HiltViewModel
class CreateWarrantyViewModel @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository,
    private val dateFormatProvider: AppDateFormatProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateWarrantyUiState())
    val uiState: StateFlow<CreateWarrantyUiState> = _uiState.asStateFlow()

    fun onProductNameChange(newValue: String) {
        _uiState.update { it.copy(productName = newValue) }
    }

    fun onBrandNameChange(newValue: String) {
        _uiState.update { it.copy(brand = newValue) }
    }

    fun onStoreNameChange(newValue: String) {
        _uiState.update { it.copy(storeName = newValue) }
    }

    fun onNotesChange(newValue: String) {
        _uiState.update { it.copy(notes = newValue) }
    }

    fun onPriceChange(newValue: String) {
        _uiState.update { it.copy(price = newValue) }
    }

    fun onCurrencyChange(newValue: String) {
        _uiState.update { it.copy(selectedCurrency = newValue) }
    }

    fun onPurchaseDateSelected(newValue: Long?) {
        val date = millisToLocalDate(newValue)
        _uiState.update {
            it.copy(
                purchaseDate = date,
                purchaseDateText = date.format(dateFormatProvider.shortDate)
            )
        }
    }

    fun onExpirationDateSelected(newValue: Long?) {
        val date = millisToLocalDate(newValue)
        _uiState.update {
            it.copy(
                expirationDate = date,
                expirationDateText = date.format(dateFormatProvider.shortDate)
            )
        }
    }

    private fun millisToLocalDate(millis: Long?): LocalDate {
        return if (millis != null) {
            Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } else {
            Instant.now()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
    }


}