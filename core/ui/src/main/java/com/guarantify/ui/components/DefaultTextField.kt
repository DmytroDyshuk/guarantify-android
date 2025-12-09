package com.guarantify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.guarantify.ui.theme.GuarantifyTheme

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true
    )
}

@Preview
@Composable
fun AppTextFieldPreview() {
    GuarantifyTheme() {
        Surface {
            AppTextField(
                value = "",
                onValueChange = {},
                label = "Preview"
            )
        }
    }
}