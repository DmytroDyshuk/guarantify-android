package com.guarantify.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.guarantify.auth.AuthScreen

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    composable<AuthDestinations.Auth> {
        AuthScreen()
    }
}