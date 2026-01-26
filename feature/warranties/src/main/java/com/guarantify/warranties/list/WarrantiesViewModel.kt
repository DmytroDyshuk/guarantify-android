package com.guarantify.warranties.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guarantify.domain.repository.WarrantiesRepository
import com.guarantify.warranties.list.state.WarrantiesUiState
import com.guarantify.warranties.mapper.WarrantyUiMapper
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
    warrantiesRepository: WarrantiesRepository,
    private val warrantyUiMapper: WarrantyUiMapper
) : ViewModel() {

    val uiState: StateFlow<WarrantiesUiState> = warrantiesRepository.latestWarranties
        .map { warranties ->
            if (warranties.isEmpty()) WarrantiesUiState.Empty
            else WarrantiesUiState.Success(warranties.map(warrantyUiMapper::map))
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