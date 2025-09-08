package com.guarantify.destinations

import kotlinx.serialization.Serializable

sealed class RootDestinations {

    @Serializable
    data object Auth : RootDestinations()

    @Serializable
    data object Home : RootDestinations()

}