package com.guarantify.warranties.list.state

import com.guarantify.warranties.list.model.WarrantyListItemUi

sealed interface WarrantiesUiState {
    object Loading : WarrantiesUiState
    data class Success(val warranties: List<WarrantyListItemUi>) : WarrantiesUiState
    data class Error(val message: String) : WarrantiesUiState
    object Empty : WarrantiesUiState
}