package com.guarantify.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    isAuthorized: Boolean
) {
    val startDestination = if (isAuthorized) {
        RootDestinations.Home
    } else RootDestinations.Auth

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

    }
}