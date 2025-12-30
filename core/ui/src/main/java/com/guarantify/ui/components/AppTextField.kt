package com.guarantify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AppOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String? = null,
    singleLine: Boolean = true,
    prefix: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxCharacters: Int? = null
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {
            if (maxCharacters != null) {
                if (it.length <= maxCharacters) onValueChange(it)
            } else onValueChange(it)
        },
        label = { Text(label) },
        placeholder = { placeholder?.let { Text(it) } },
        singleLine = singleLine,
        prefix = { prefix?.let { Text(it) } },
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        supportingText = maxCharacters?.let {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "${value.length} / $maxCharacters",
                    textAlign = TextAlign.End
                )
            }
        }

    )
}