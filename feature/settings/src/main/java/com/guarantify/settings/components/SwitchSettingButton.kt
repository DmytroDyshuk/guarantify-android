package com.guarantify.settings.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SwitchSettingButton(
    modifier: Modifier = Modifier,
    headlineText: String,
    leadingIcon: ImageVector,
    checked: Boolean,
    onCheck: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = 12.dp),
            imageVector = leadingIcon,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 32.dp),
            text = headlineText,
            style = MaterialTheme.typography.titleMedium
        )
        Switch(
            modifier = Modifier.padding(end = 12.dp),
            checked = checked,
            onCheckedChange = { isChecked ->
                onCheck(isChecked)
            }
        )
    }
}