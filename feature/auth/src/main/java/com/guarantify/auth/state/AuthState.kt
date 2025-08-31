package com.guarantify.auth.state

data class AuthState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)