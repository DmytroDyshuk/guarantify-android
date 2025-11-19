package com.guarantify.warranties

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guarantify.domain.model.Warranty
import com.guarantify.ui.components.LoadingScreen
import com.guarantify.warranties.components.WarrantyItem
import com.guarantify.warranties.model.WarrantiesUiState

@Composable
fun WarrantiesScreen(viewModel: WarrantiesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        WarrantiesUiState.Loading -> LoadingScreen()
        WarrantiesUiState.Empty -> {
            WarrantiesEmptyScreenContent()
        }

        is WarrantiesUiState.Error -> {
            //TODO: show toast? or just a string text?
            val errorMessage = (uiState as WarrantiesUiState.Error).message
        }

        is WarrantiesUiState.Success -> {
            val warrantiesList = (uiState as WarrantiesUiState.Success).warranties
            WarrantiesScreenContent(warranties = warrantiesList)
        }
    }
}

@Composable
fun WarrantiesScreenContent(
    modifier: Modifier = Modifier,
    warranties: List<Warranty>
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState
    ) {
        items(items = warranties, key = { it.id }) {
            WarrantyItem(warranty = it)
        }
    }
}

@Composable
fun WarrantiesEmptyScreenContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.warranties_screen_empty_message),
            style = MaterialTheme.typography.labelMedium
        )
    }
}