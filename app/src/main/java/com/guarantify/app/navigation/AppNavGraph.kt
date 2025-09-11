package com.guarantify.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.guarantify.auth.navigation.AuthDestinations
import com.guarantify.auth.navigation.authGraph
import com.guarantify.home_navigation.HomeScreenContainer

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
        navigation<RootDestinations.Auth>(startDestination = AuthDestinations.Auth) {
            authGraph(navController)
        }
        composable<RootDestinations.Home> {
            HomeScreenContainer()
        }
    }
}