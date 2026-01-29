package com.guarantify.app.navigation

import kotlinx.serialization.Serializable

sealed class RootDestinations {

    @Serializable
    data object Auth : RootDestinations()

    @Serializable
    data object Home : RootDestinations()

    @Serializable
    data object CreateWarranty : RootDestinations()

    @Serializable
    data object WarrantyDetails : RootDestinations()

}