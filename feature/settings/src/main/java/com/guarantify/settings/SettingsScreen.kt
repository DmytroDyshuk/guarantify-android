package com.guarantify.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

    }
}

@Composable
fun SettingButton(
    headlineText: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = headlineText
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.rounded_arrow_right_24),
            contentDescription = null
        )
    }
}