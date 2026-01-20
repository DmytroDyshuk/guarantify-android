package com.guarantify.warranties.list.state

import com.guarantify.warranties.model.WarrantyUiModel

sealed interface WarrantiesUiState {
    object Loading : WarrantiesUiState
    data class Success(val warranties: List<WarrantyUiModel>) : WarrantiesUiState
    data class Error(val message: String) : WarrantiesUiState
    object Empty : WarrantiesUiState
}