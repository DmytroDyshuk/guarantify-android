package com.guarantify.home_navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.guarantify.home_navigation.bottomnavigation.NavigationBottomBar
import com.guarantify.insights.InsightsScreen
import com.guarantify.settings.SettingsScreen
import com.guarantify.warranties.navigation.WarrantiesDestinations
import com.guarantify.warranties.navigation.warrantiesGraph

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreenContainer() {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            currentDestination?.let {
                when {
                    it.hasRoute<WarrantiesDestinations.WarrantiesScreen>() -> {
                        TopAppBar(
                            title = { Text(text = stringResource(R.string.warranties_topbar_title)) }
                        )
                    }

                    it.hasRoute<HomeDestinations.Insights>() -> {
                        TopAppBar(
                            title = { Text(text = stringResource(R.string.insights_topbar_title)) }
                        )
                    }

                    it.hasRoute<HomeDestinations.Settings>() -> {
                        TopAppBar(
                            title = { Text(text = stringResource(R.string.settings_topbar_title)) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            currentDestination?.let {
                AnimatedVisibility(
                    visible = it.hasRoute<WarrantiesDestinations.WarrantiesScreen>(),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    FloatingActionButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        },
        bottomBar = {
            NavigationBottomBar(
                navController = homeNavController,
                currentDestination = currentDestination
            )
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