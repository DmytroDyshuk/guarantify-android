package com.guarantify.warranties.details.state

sealed interface DetailsUiState {
    object Loading : DetailsUiState
    data class Content(val warranty: WarrantyDetailsUi) : DetailsUiState
    object Error : DetailsUiState
}
