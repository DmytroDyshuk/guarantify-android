package com.guarantify.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.guarantify.auth.navigation.AuthDestinations
import com.guarantify.auth.navigation.authGraph
import com.guarantify.domain.model.AuthState
import com.guarantify.home_navigation.HomeScreenContainer
import com.guarantify.warranties.create.CreateWarrantyScreen
import com.guarantify.warranties.details.WarrantyDetailsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    authState: AuthState
) {

    val startDestination = when (authState) {
        is AuthState.Authenticated -> RootDestinations.Home
        is AuthState.Unauthenticated -> RootDestinations.Auth
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        navigation<RootDestinations.Auth>(startDestination = AuthDestinations.Auth) {
            authGraph(navController)
        }

        composable<RootDestinations.Home> {
            HomeScreenContainer(
                onNavigateToCreateWarranty = {
                    navController.navigate(RootDestinations.CreateWarranty) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<RootDestinations.CreateWarranty> {
            CreateWarrantyScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                onWarrantyCreated = {
                    navController.popBackStack()
                }
            )
        }

        composable<RootDestinations.WarrantyDetails> {
            WarrantyDetailsScreen()
        }
    }
}