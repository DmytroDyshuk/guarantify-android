package com.guarantify.auth.navigation

import kotlinx.serialization.Serializable

sealed class AuthDestinations {

    @Serializable
    data object Auth : AuthDestinations()

}