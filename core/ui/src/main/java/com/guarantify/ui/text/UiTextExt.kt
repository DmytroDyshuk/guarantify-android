package com.guarantify.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun UiText.asString(): String {
    return when (this) {
        is UiText.Plain -> value
        is UiText.Res -> stringResource(id, *args.toTypedArray())
    }
}