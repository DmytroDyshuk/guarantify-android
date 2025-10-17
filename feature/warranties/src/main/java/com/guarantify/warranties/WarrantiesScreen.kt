package com.guarantify.warranties

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WarrantiesScreen(viewModel: WarrantiesViewModel = hiltViewModel()) {
    WarrantiesScreenContent()
}

@Composable
fun WarrantiesScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Work in progress",
        )
    }
}