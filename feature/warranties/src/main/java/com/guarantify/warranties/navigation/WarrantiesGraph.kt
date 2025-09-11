package com.guarantify.warranties.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.guarantify.warranties.WarrantiesScreen

fun NavGraphBuilder.warrantiesGraph(navHostController: NavHostController) {
    composable<WarrantiesDestinations.WarrantiesScreen> {
        WarrantiesScreen()
    }
}