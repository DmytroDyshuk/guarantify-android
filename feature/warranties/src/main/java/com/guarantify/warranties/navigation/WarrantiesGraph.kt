package com.guarantify.warranties.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.guarantify.warranties.create.CreateWarrantyScreen
import com.guarantify.warranties.list.WarrantiesScreen

fun NavGraphBuilder.warrantiesGraph(
    navHostController: NavHostController,
    onShowAddWarrantyDialog: () -> Unit
) {
    composable<WarrantiesDestinations.WarrantiesScreen> {
        WarrantiesScreen(
            onShowAddWarrantyDialog = onShowAddWarrantyDialog
        )
    }
    composable<WarrantiesDestinations.CreateWarranty> {
        CreateWarrantyScreen(
            onBackCLicked = {
                navHostController.popBackStack()
            }
        )
    }
}