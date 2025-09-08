package com.guarantify.home_navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.guarantify.destinations.RootDestinations

fun NavGraphBuilder.homeGraph() {
    composable<RootDestinations.Home> {
        HomeScreenContainer()
    }
}