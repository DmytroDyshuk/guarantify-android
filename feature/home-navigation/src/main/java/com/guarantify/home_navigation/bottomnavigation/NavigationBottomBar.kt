package com.guarantify.home_navigation.bottomnavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import com.guarantify.home_navigation.BottomNavItem
import com.guarantify.home_navigation.HomeDestinations
import com.guarantify.warranties.navigation.WarrantiesDestinations

@Composable
fun NavigationBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    //TODO: test the reliability of the display bottom bar logic
//    val isTopLevelDestination = BottomNavItem.entries.map { bottomNavItem ->
//        bottomNavItem.route::class
//    }.any { routeClass ->
//        currentDestination?.hierarchy?.any { it.hasRoute(routeClass) } == true
//    }
//    val isStartDestination =
//        currentDestination?.parent?.startDestinationRoute == currentDestination?.route
//    val showBottomBar = isTopLevelDestination && isStartDestination

    val topLevelDestinations = setOf(
        WarrantiesDestinations.WarrantiesScreen::class,
        HomeDestinations.Insights::class,
        HomeDestinations.Settings::class
    )
    val showBottomBar = topLevelDestinations.any {
        currentDestination?.hasRoute(it) == true
    }

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
                    label = {
                        Text(
                            text = bottomNavItem.label
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