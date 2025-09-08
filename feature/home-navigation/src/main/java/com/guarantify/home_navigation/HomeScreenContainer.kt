package com.guarantify.home_navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guarantify.home_navigation.bottomnavigation.NavigationBottomBar

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
            startDestination = HomeDestinations.WarrantiesList,
        ) {
            composable<HomeDestinations.WarrantiesList> {
                //TODO: WarrantiesScreen()
            }
            composable<HomeDestinations.Insights> {
                //TODO: InsightsScreen()
            }
            composable<HomeDestinations.Settings> {
                //TODO: SettingsScreen()
            }
        }
    }
}