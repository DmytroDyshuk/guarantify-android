package com.guarantify.home_navigation.bottomnavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.guarantify.home_navigation.components.BottomNavItem
import com.guarantify.navigation.destinations.HomeDestinations


private val TOP_LEVEL_DESTINATIONS = setOf(
    HomeDestinations.Warranties::class,
    HomeDestinations.Insights::class,
    HomeDestinations.Settings::class
)

@Composable
fun NavigationBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    AnimatedVisibility(
        visible = currentDestination?.isTopLevel() == true,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it }
    ) {
        NavigationBar(modifier = modifier) {
            BottomNavItem.entries.forEach { bottomNavItem ->
                val isSelected = currentDestination?.hierarchy?.any {
                    it.hasRoute(bottomNavItem.route::class)
                } == true

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(bottomNavItem.icon),
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(bottomNavItem.label)
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(bottomNavItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun NavDestination?.isTopLevel(): Boolean =
    TOP_LEVEL_DESTINATIONS.any { this?.hasRoute(it) == true }