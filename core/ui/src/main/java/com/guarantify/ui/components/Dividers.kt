package com.guarantify.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PaddingHorizontalDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(modifier = modifier.padding(horizontal = 16.dp))
}