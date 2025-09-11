package com.guarantify.home_navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.guarantify.home_navigation.bottomnavigation.NavigationBottomBar
import com.guarantify.insights.InsightsScreen
import com.guarantify.settings.SettingsScreen
import com.guarantify.warranties.navigation.WarrantiesDestinations
import com.guarantify.warranties.navigation.warrantiesGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreenContainer() {
    val homeNavController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBottomBar()
        }
    ) {
        NavHost(
            navController = homeNavController,
            startDestination = HomeDestinations.Warranties,
        ) {
            navigation<HomeDestinations.Warranties>(startDestination = WarrantiesDestinations.WarrantiesScreen) {
                warrantiesGraph(homeNavController)
            }
            composable<HomeDestinations.Insights> {
                InsightsScreen()
            }
            composable<HomeDestinations.Settings> {
                SettingsScreen()
            }
        }
    }
}