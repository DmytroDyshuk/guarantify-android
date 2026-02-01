package com.guarantify.warranties.details.state

import com.guarantify.domain.model.Warranty

data class DetailsUiState(
    val warranty: Warranty? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
