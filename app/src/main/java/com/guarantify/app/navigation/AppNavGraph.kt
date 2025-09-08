package com.guarantify.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.guarantify.auth.navigation.authGraph
import com.guarantify.destinations.RootDestinations
import com.guarantify.home_navigation.homeGraph

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    isAuthorized: Boolean = false //TODO
) {
    val startDestination = if (isAuthorized) {
        RootDestinations.Home
    } else RootDestinations.Auth

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(navController)
        homeGraph()
    }
}