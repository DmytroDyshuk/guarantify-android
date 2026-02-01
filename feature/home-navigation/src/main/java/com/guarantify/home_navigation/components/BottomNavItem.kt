package com.guarantify.home_navigation.components

import androidx.annotation.StringRes
import com.guarantify.home_navigation.R
import com.guarantify.navigation.destinations.HomeDestinations

enum class BottomNavItem(
    @param:StringRes val label: Int,
    val icon: Int,
    val route: HomeDestinations
) {
    Warranties(
        label = R.string.warranties_label,
        icon = R.drawable.outline_receipt_long_24,
        route = HomeDestinations.Warranties
    ),
    Insights(
        label = R.string.insights_label,
        icon = R.drawable.outline_search_insights_24,
        route = HomeDestinations.Insights
    ),
    Settings(
        label = R.string.settings_label,
        icon = R.drawable.outline_settings_24,
        route = HomeDestinations.Settings
    )
}