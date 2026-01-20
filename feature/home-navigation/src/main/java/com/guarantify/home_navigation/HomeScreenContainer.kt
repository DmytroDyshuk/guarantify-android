package com.guarantify.home_navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.guarantify.home_navigation.bottomnavigation.NavigationBottomBar
import com.guarantify.home_navigation.components.HomeTopAppBar
import com.guarantify.insights.InsightsScreen
import com.guarantify.settings.SettingsScreen
import com.guarantify.warranties.list.components.WarrantyCreationMethodDialog
import com.guarantify.warranties.navigation.WarrantiesDestinations
import com.guarantify.warranties.navigation.warrantiesGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContainer() {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination
    var showMethodDialog by rememberSaveable { mutableStateOf(false) }

    if (showMethodDialog) {
        WarrantyCreationMethodDialog(
            onDismissRequest = {
                showMethodDialog = false
            },
            onCreateManually = {
                showMethodDialog = false
                homeNavController.navigate(WarrantiesDestinations.CreateWarranty)
            },
            onScan = {
                showMethodDialog = false
                //TODO
            }
        )
    }

    Scaffold(
        topBar = {
            currentDestination?.let {
                when {
                    it.hasRoute<WarrantiesDestinations.WarrantiesScreen>() -> {
                        HomeTopAppBar(
                            title = stringResource(R.string.warranties_topbar_title)
                        )
                    }

                    it.hasRoute<HomeDestinations.Insights>() -> {
                        HomeTopAppBar(
                            title = stringResource(R.string.insights_topbar_title)
                        )
                    }

                    it.hasRoute<HomeDestinations.Settings>() -> {
                        HomeTopAppBar(
                            title = stringResource(R.string.settings_topbar_title)
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
                    FloatingActionButton(onClick = {
                        showMethodDialog = true
                    }) {
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
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = homeNavController,
            startDestination = HomeDestinations.Warranties,
        ) {
            navigation<HomeDestinations.Warranties>(startDestination = WarrantiesDestinations.WarrantiesScreen) {
                warrantiesGraph(
                    navHostController = homeNavController,
                    onShowAddWarrantyDialog = {
                        showMethodDialog = true
                    }
                )
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