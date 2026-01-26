package com.guarantify.warranties.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guarantify.ui.R
import com.guarantify.ui.theme.darkGrayishCyan
import com.guarantify.ui.theme.emeraldGreen
import com.guarantify.ui.theme.lavenderGray

@Composable
fun WarrantyDetailsScreen(

    viewModel: WarrantyDetailsViewModel = hiltViewModel()
) {
    WarrantyDetailsScreenContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantyDetailsScreenContent() {
    val cardInnerHorizontalPadding = 26.dp

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Warranty details") //TODO: display warranty name/brand
                },
                navigationIcon = {
                    IconButton(onClick = { /* todo: implement back */ }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = cardInnerHorizontalPadding)
                        .padding(top = 12.dp),
                    text = "MacBook Air M1",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    modifier = Modifier.padding(horizontal = cardInnerHorizontalPadding),
                    text = buildAnnotatedString {
                        append("Brand")
                        append(" · ")
                        append("Store")
                    },
                    style = MaterialTheme.typography.labelMedium
                )
                SuggestionChip( //TODO: change bg color with days left
                    modifier = Modifier
                        .padding(horizontal = cardInnerHorizontalPadding)
                        .padding(top = 8.dp),
                    label = {
                        Text(
                            text = "143 days left",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    onClick = {},
                    border = null,
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = emeraldGreen
                    )
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = cardInnerHorizontalPadding)
                        .padding(bottom = 6.dp),
                    text = "Warranty until 12 Dec 2026",
                    color = darkGrayishCyan,
                    style = MaterialTheme.typography.labelMedium
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = cardInnerHorizontalPadding)
                        .padding(bottom = 12.dp)
                        .height(6.dp),
                    progress = { 0.35f },
                    color = emeraldGreen, //TODO: Change track color with days left
                    trackColor = lavenderGray,
                    gapSize = 1.dp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WarrantyDetailsScreenPreview() {
    WarrantyDetailsScreenContent()
}