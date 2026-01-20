package com.guarantify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppDateField(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    value: String,
    label: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = true,
            singleLine = true,
            label = label?.let { { Text(it, maxLines = 1) } },
            trailingIcon = {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
            },
            isError = isError,
            supportingText = if (isError) {
                {
                    Text(
                        text = errorMessage ?: "Required",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else null
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}
