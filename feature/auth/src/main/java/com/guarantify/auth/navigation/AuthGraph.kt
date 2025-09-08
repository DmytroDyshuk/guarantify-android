package com.guarantify.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.guarantify.auth.AuthScreen
import com.guarantify.destinations.RootDestinations

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<RootDestinations.Auth>(startDestination = AuthDestinations.Auth) {
        composable<AuthDestinations.Auth> {
            AuthScreen()
        }
    }
}