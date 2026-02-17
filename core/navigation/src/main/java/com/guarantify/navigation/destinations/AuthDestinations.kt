package com.guarantify.navigation.destinations

import kotlinx.serialization.Serializable

sealed class AuthDestinations {

    @Serializable
    data object Auth : AuthDestinations()

}