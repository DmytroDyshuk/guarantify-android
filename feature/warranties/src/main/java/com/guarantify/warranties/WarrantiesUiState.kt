package com.guarantify.warranties

import com.guarantify.domain.model.Warranty

sealed interface WarrantiesUiState {
    object Loading : WarrantiesUiState
    data class Success(val warranties: List<Warranty>) : WarrantiesUiState
    data class Error(val message: String) : WarrantiesUiState
    object Empty : WarrantiesUiState
}