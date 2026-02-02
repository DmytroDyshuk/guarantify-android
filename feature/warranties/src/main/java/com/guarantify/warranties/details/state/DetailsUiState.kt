package com.guarantify.warranties.details.state

import com.guarantify.domain.model.Warranty

sealed interface DetailsUiState {
    object Loading : DetailsUiState
    data class Content(val warranty: Warranty) : DetailsUiState
    object Error : DetailsUiState
}
