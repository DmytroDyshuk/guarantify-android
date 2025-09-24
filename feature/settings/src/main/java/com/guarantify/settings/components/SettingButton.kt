package com.guarantify.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guarantify.settings.R
import com.guarantify.ui.theme.GuarantifyTheme

@Composable
fun SettingButton(
    modifier: Modifier = Modifier,
    headlineText: String,
    leadingIcon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(
                enabled = true,
                onClick = onClick
            ),
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
        Icon(
            modifier = Modifier.padding(end = 12.dp),
            imageVector = ImageVector.vectorResource(R.drawable.rounded_arrow_right_24),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingButton() {
    GuarantifyTheme(darkTheme = false) {
        Surface {
            SettingButton(
                headlineText = "Headline text",
                leadingIcon = ImageVector.vectorResource(R.drawable.outline_heart_plus_24),
                onClick = {}
            )
        }
    }
}