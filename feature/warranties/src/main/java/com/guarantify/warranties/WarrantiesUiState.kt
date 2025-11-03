package com.guarantify.warranties

import com.guarantify.domain.model.Warranty

data class WarrantiesUiState(
    val isLoading: Boolean = false,
    val warranties: List<Warranty> = emptyList(),
    val errorMessage: String? = null
)