package com.guarantify.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.guarantify.auth.navigation.authGraph
import com.guarantify.domain.model.auth.AuthState
import com.guarantify.home_navigation.HomeScreenContainer
import com.guarantify.navigation.destinations.AuthDestinations
import com.guarantify.navigation.destinations.RootDestinations
import com.guarantify.warranties.create.CreateWarrantyScreen
import com.guarantify.warranties.details.WarrantyDetailsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    authState: AuthState
) {
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> navController.navigate(RootDestinations.Home) {
                popUpTo(0)
                launchSingleTop = true
            }

            is AuthState.Unauthenticated -> navController.navigate(RootDestinations.Auth) {
                popUpTo(0)
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = RootDestinations.Auth
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
                },
                onNavigateToWarrantyDetails = { warrantyId ->
                    navController.navigate(RootDestinations.WarrantyDetails(id = warrantyId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<RootDestinations.CreateWarranty> {
            CreateWarrantyScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateWarranty = {
                    navController.popBackStack()
                }
            )
        }

        composable<RootDestinations.WarrantyDetails> {
            WarrantyDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}