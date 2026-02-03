package com.guarantify.app

import com.guarantify.domain.model.auth.AuthState

sealed interface MainActivityUiState {

    data object Loading : MainActivityUiState

    data class Success(val authState: AuthState) : MainActivityUiState

    fun shouldKeepSplashScreen() = this is Loading

}