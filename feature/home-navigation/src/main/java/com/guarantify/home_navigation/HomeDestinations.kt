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
    Warranties(
        label = "Warranties",
        icon = R.drawable.outline_receipt_24,
        route = HomeDestinations.Warranties
    ),
    Insights(
        label = "Insights",
        icon = R.drawable.outline_search_insights_24,
        route = HomeDestinations.Insights
    ),
    Settings(
        label = "Settings",
        icon = R.drawable.outline_settings_24,
        route = HomeDestinations.Settings
    )
}


