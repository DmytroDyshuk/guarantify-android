package com.guarantify.warranties.create.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.guarantify.domain.repository.WarrantiesRepository
import com.guarantify.warranties.create.state.CreateWarrantyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class CreateWarrantyViewModel @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository
) : ViewModel() {
    var uiState by mutableStateOf(CreateWarrantyUiState())
        private set

    fun onProductNameChange(newValue: String) {
        uiState = uiState.copy(productName = newValue)
    }

    fun onBrandNameChange(newValue: String) {
        uiState = uiState.copy(brand = newValue)
    }

    fun onStoreNameChange(newValue: String) {
        uiState = uiState.copy(storeName = newValue)
    }

    fun onNotesChange(newValue: String) {
        uiState = uiState.copy(notes = newValue)
    }
}