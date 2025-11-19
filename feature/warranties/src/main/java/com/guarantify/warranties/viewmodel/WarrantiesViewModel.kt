package com.guarantify.warranties.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guarantify.domain.repository.WarrantiesRepository
import com.guarantify.ui.di.AppDateFormatProvider
import com.guarantify.warranties.mapper.toWarrantyUiModel
import com.guarantify.warranties.model.WarrantiesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class WarrantiesViewModel @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository,
    private val dateFormatter: AppDateFormatProvider
) : ViewModel() {

    val uiState: StateFlow<WarrantiesUiState> = warrantiesRepository.latestWarranties
        .map { warranties ->
            if (warranties.isEmpty()) {
                WarrantiesUiState.Empty
            } else {
                val uiWarranties = warranties.map { it.toWarrantyUiModel(dateFormatter.longDate) }
                WarrantiesUiState.Success(warranties = uiWarranties)
            }
        }
        .distinctUntilChanged()
        .catch {
            emit(WarrantiesUiState.Error(message = it.message ?: "Something went wrong"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WarrantiesUiState.Loading
        )

}