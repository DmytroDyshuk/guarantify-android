package com.guarantify.home_navigation.bottomnavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.guarantify.home_navigation.BottomNavItem

@Composable
fun NavigationBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    val isTopLevelDestination = BottomNavItem.entries.map { bottomNavItem ->
        bottomNavItem.route::class
    }.any { routeClass ->
        currentDestination?.hierarchy?.any { it.hasRoute(routeClass) } == true
    }
    val isStartDestination =
        currentDestination?.parent?.startDestinationRoute == currentDestination?.route
    val showBottomBar = isTopLevelDestination && isStartDestination

    AnimatedVisibility(showBottomBar) {
        NavigationBar(
            modifier = modifier
        ) {
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
                    selected = isSelected,
                    onClick = {
                        navController.navigate(bottomNavItem.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                )
            }
        }
    }
}