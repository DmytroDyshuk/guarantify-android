package com.guarantify.navigation.destinations

import kotlinx.serialization.Serializable

sealed class HomeDestinations {

    @Serializable
    data object Warranties : HomeDestinations()

    @Serializable
    data object Insights : HomeDestinations()

    @Serializable
    data object Settings : HomeDestinations()

}