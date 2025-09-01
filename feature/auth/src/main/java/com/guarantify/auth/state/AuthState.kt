package com.guarantify.auth.state

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)