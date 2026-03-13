package com.guarantify.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guarantify.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = authRepository.observeAuthState()
        .map {
            MainActivityUiState.Success(it)
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )

}