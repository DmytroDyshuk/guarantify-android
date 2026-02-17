package com.guarantify.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.guarantify.auth.AuthScreen
import com.guarantify.navigation.destinations.AuthDestinations

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    composable<AuthDestinations.Auth> {
        AuthScreen()
    }
}