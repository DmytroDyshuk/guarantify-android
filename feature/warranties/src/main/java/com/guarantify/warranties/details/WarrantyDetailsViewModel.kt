package com.guarantify.warranties.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.guarantify.common.result.Result
import com.guarantify.domain.repository.WarrantiesRepository
import com.guarantify.navigation.destinations.RootDestinations
import com.guarantify.warranties.details.state.DetailsUiState
import com.guarantify.warranties.mapper.WarrantyUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WarrantyDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    warrantiesRepository: WarrantiesRepository,
    warrantyUiMapper: WarrantyUiMapper
) : ViewModel() {

    private val warrantyId = savedStateHandle.toRoute<RootDestinations.WarrantyDetails>().id

    private val _uiState: MutableStateFlow<DetailsUiState> =
        MutableStateFlow(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading
            when (val result = warrantiesRepository.getWarranty(warrantyId)) {
                is Result.Success -> _uiState.value =
                    DetailsUiState.Content(warrantyUiMapper.toDetails(result.data))

                is Result.Error -> _uiState.value = DetailsUiState.Error
                is Result.Loading -> _uiState.value = DetailsUiState.Loading
            }
        }
    }

}