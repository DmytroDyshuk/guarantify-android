package com.guarantify.domain.model.auth

sealed interface AuthState {
    data object Unauthenticated : AuthState
    data object Authenticated : AuthState //TODO: add UserData
}