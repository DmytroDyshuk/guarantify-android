package com.guarantify.warranties.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.guarantify.ui.components.LoadingScreen
import com.guarantify.ui.components.NoResultsScreen
import com.guarantify.warranties.R
import com.guarantify.warranties.list.components.WarrantyItem
import com.guarantify.warranties.list.state.WarrantiesUiState
import com.guarantify.warranties.list.model.WarrantyListItemUi

@Composable
fun WarrantiesScreen(
    viewModel: WarrantiesViewModel = hiltViewModel(),
    onShowAddWarrantyDialog: () -> Unit,
    onWarrantyClick: (id: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        WarrantiesUiState.Loading -> LoadingScreen()
        WarrantiesUiState.Empty -> {
            WarrantiesEmptyScreenContent(
                onAddWarrantyClicked = onShowAddWarrantyDialog
            )
        }

        is WarrantiesUiState.Error -> {
            NoResultsScreen()
        }

        is WarrantiesUiState.Success -> {
            val warrantiesList = (uiState as WarrantiesUiState.Success).warranties
            WarrantiesScreenContent(warranties = warrantiesList) {
                onWarrantyClick(it)
            }
        }
    }
}

@Composable
fun WarrantiesScreenContent(
    modifier: Modifier = Modifier,
    warranties: List<WarrantyListItemUi>,
    onWarrantyClick: (id: String) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = warranties, key = { it.id }) { warrantyUiModel ->
            WarrantyItem(warranty = warrantyUiModel) { id ->
                onWarrantyClick(id)
            }
        }
    }
}

@Composable
fun WarrantiesEmptyScreenContent(
    modifier: Modifier = Modifier,
    onAddWarrantyClicked: () -> Unit
) {
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.add_warranty)
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(200.dp),
            composition = lottieComposition,
            iterations = LottieConstants.IterateForever,
            speed = 0.8f
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.warranties_screen_empty_message),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAddWarrantyClicked
        ) {
            Text(
                text = stringResource(R.string.add_first_warranty)
            )
        }
    }

}