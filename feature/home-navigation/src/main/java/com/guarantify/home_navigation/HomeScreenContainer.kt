package com.guarantify.home_navigation

import androidx.annotation.StringRes
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.guarantify.home_navigation.bottomnavigation.NavigationBottomBar
import com.guarantify.home_navigation.components.HomeTopAppBar
import com.guarantify.insights.InsightsScreen
import com.guarantify.navigation.destinations.HomeDestinations
import com.guarantify.settings.SettingsScreen
import com.guarantify.warranties.list.WarrantiesScreen
import com.guarantify.warranties.list.components.WarrantyCreationMethodDialog
import androidx.navigation.NavDestination.Companion.hasRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContainer(
    onNavigateToCreateWarranty: () -> Unit,
    onNavigateToWarrantyDetails: (id: String) -> Unit
) {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentDestination by remember { derivedStateOf { navBackStackEntry?.destination } }
    var showCreationMethodDialog by rememberSaveable { mutableStateOf(false) }

    if (showCreationMethodDialog) {
        WarrantyCreationMethodDialog(
            onDismissRequest = {
                showCreationMethodDialog = false
            },
            onCreateManually = {
                showCreationMethodDialog = false
                onNavigateToCreateWarranty()
            },
            onScan = {
                showCreationMethodDialog = false
                //TODO
            }
        )
    }

    Scaffold(
        topBar = {
            val titleRes = topBarTitleRes(currentDestination)
            if (titleRes != null) {
                HomeTopAppBar(title = titleRes)
            }
        },
        floatingActionButton = {
            currentDestination?.let {
                AnimatedVisibility(
                    visible = it.hasRoute<HomeDestinations.Warranties>(),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    FloatingActionButton(onClick = {
                        showCreationMethodDialog = true
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
            composable<HomeDestinations.Warranties> {
                WarrantiesScreen(
                    onShowAddWarrantyDialog = {
                        showCreationMethodDialog = true
                    },
                    onWarrantyClick = { warrantyId ->
                        onNavigateToWarrantyDetails(warrantyId)
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

@StringRes
private fun topBarTitleRes(dest: NavDestination?): Int? = when {
    dest?.hasRoute<HomeDestinations.Warranties>() == true -> R.string.warranties_topbar_title
    dest?.hasRoute<HomeDestinations.Insights>() == true -> R.string.insights_topbar_title
    dest?.hasRoute<HomeDestinations.Settings>() == true -> R.string.settings_topbar_title
    else -> null
}