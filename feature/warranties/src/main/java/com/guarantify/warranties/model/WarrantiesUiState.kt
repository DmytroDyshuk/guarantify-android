package com.guarantify.warranties.model

sealed interface WarrantiesUiState {
    object Loading : WarrantiesUiState
    data class Success(val warranties: List<WarrantyUiModel>) : WarrantiesUiState
    data class Error(val message: String) : WarrantiesUiState
    object Empty : WarrantiesUiState
}