package com.guarantify.navigation.destinations

import kotlinx.serialization.Serializable

sealed class RootDestinations {

    @Serializable
    data object Auth : RootDestinations()

    @Serializable
    data object Home : RootDestinations()

    @Serializable
    data object CreateWarranty : RootDestinations()

    @Serializable
    data class WarrantyDetails(val id: String) : RootDestinations()

}