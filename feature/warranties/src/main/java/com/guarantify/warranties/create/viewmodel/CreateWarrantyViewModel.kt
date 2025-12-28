package com.guarantify.warranties.create.viewmodel

import androidx.lifecycle.ViewModel
import com.guarantify.domain.repository.WarrantiesRepository
import com.guarantify.warranties.create.state.CreateWarrantyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class CreateWarrantyViewModel @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository
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

    fun onPurchaseDateChange(newValue: Long) {
        _uiState.update { it.copy(purchaseDate = newValue) }
    }
}