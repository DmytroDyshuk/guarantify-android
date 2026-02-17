package com.guarantify.warranties.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.guarantify.ui.R
import com.guarantify.ui.components.LoadingScreen
import com.guarantify.ui.components.NoResultsScreen
import com.guarantify.ui.theme.darkGrayishCyan
import com.guarantify.ui.theme.lavenderGray
import com.guarantify.warranties.details.state.DetailsUiState
import com.guarantify.warranties.details.state.WarrantyDetailsUi
import com.guarantify.warranties.extensions.toColor

@Composable
fun WarrantyDetailsScreen(
    viewModel: WarrantyDetailsViewModel = hiltViewModel(), onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            WarrantyDetailsTopAppBar(
                onBackClick = onBackClick
            )
        }) { paddingValues ->
        when (val state = uiState) {
            is DetailsUiState.Loading -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }

            is DetailsUiState.Error -> {
                NoResultsScreen(modifier = Modifier.padding(paddingValues))
            }

            is DetailsUiState.Content -> {
                WarrantyDetailsScreenContent(
                    warranty = state.warranty, modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun WarrantyDetailsScreenContent(
    warranty: WarrantyDetailsUi, modifier: Modifier = Modifier
) {
    val statusColor by remember { mutableStateOf(warranty.status.toColor()) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = warranty.productName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = warranty.brand ?: "—",
                    style = MaterialTheme.typography.labelMedium
                )
                SuggestionChip(
                    modifier = Modifier.padding(top = 8.dp),
                    label = {
                        Text(
                            text = "${warranty.remainingDays} days left",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    onClick = {},
                    border = null,
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = statusColor
                    )
                )
                Text(
                    modifier = Modifier.padding(bottom = 6.dp),
                    text = "Warranty until ${warranty.expirationDate}",
                    color = darkGrayishCyan,
                    style = MaterialTheme.typography.labelMedium
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .height(6.dp),
                    progress = {
                        warranty.warrantyExpirationProgress
                    },
                    color = statusColor,
                    trackColor = lavenderGray,
                    gapSize = 1.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoCardWithTitle(title = "Details") {
            DoubleStringInfoRow(
                icon = ImageVector.vectorResource(R.drawable.ic_store),
                firstString = "Store",
                secondString = warranty.store ?: "—"
            )
            HorizontalDivider()
            DoubleStringInfoRow(
                icon = ImageVector.vectorResource(R.drawable.ic_price),
                firstString = "Price",
                secondString = warranty.priceText ?: "—"
            )
            HorizontalDivider()
            DoubleStringInfoRow(
                icon = ImageVector.vectorResource(R.drawable.ic_brand),
                firstString = "Brand",
                secondString = warranty.brand ?: "—"
            )
            HorizontalDivider()
            DoubleStringInfoRow(
                icon = ImageVector.vectorResource(R.drawable.ic_serial_number),
                firstString = "Serial Number",
                secondString = warranty.serialNumber ?: "—"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoCardWithTitle(title = "Dates") {
            DoubleStringInfoRow(
                icon = ImageVector.vectorResource(R.drawable.ic_purchase_date),
                firstString = "Purchase Date",
                secondString = warranty.purchaseDate
            )
            HorizontalDivider()
            DoubleStringInfoRow(
                icon = ImageVector.vectorResource(R.drawable.ic_end_time_hourglass),
                firstString = "Expiration Date",
                secondString = warranty.expirationDate
            )
        }

        if (warranty.photoUrl != null) {
            Spacer(modifier = Modifier.height(16.dp))
            InfoCardWithTitle(
                title = "Attachments"
            ) {
                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(120.dp)
                ) {
                    AsyncImage(
                        model = warranty.photoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoCardWithTitle(title = "Notes") {
            Text(
                modifier = Modifier.padding(
                    vertical = 8.dp
                ),
                text = warranty.notes ?: "—"
            )
        }
    }
}

@Composable
private fun InfoCardWithTitle(
    modifier: Modifier = Modifier, title: String, content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider()
            content()
        }
    }
}

@Composable
private fun DoubleStringInfoRow(
    modifier: Modifier = Modifier, icon: ImageVector, firstString: String, secondString: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, contentDescription = null
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = firstString,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = secondString,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WarrantyDetailsTopAppBar(
    onBackClick: () -> Unit
) {
    var isExpandedDropdownMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = { Text("Warranty details") },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            Box {
                IconButton(
                    onClick = { isExpandedDropdownMenu = !isExpandedDropdownMenu }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_more_vert),
                        contentDescription = "More menu"
                    )
                }

                OptionsDropdownMenu(
                    expanded = isExpandedDropdownMenu,
                    onDismiss = { isExpandedDropdownMenu = false },
                    onEditClick = { isExpandedDropdownMenu = false },
                    onAddPhotoClick = { isExpandedDropdownMenu = false },
                    onDeleteClick = { isExpandedDropdownMenu = false }
                )
            }
        }
    )
}

@Composable
fun OptionsDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onAddPhotoClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text(text = "Edit") }, onClick = onEditClick
        )
        DropdownMenuItem(
            text = { Text(text = "Add photo") }, onClick = onAddPhotoClick
        )
        DropdownMenuItem(
            text = { Text(text = "Delete") }, onClick = onDeleteClick
        )
    }
}