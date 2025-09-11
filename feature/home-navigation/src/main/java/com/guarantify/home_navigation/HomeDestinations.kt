package com.guarantify.home_navigation

import kotlinx.serialization.Serializable

sealed class HomeDestinations {

    @Serializable
    data object Warranties : HomeDestinations()

    @Serializable
    data object Insights : HomeDestinations()

    @Serializable
    data object Settings : HomeDestinations()

}

enum class BottomNavItem(
    val label: String,
    val icon: Int,
    val route: HomeDestinations
) {
    //TODO: add WarrantiesList, Insights, Settings bottom navigation items
}


