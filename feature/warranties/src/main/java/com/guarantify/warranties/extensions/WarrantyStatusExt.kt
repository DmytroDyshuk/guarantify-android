package com.guarantify.warranties.extensions

import androidx.compose.ui.graphics.Color
import com.guarantify.ui.theme.burntOrange
import com.guarantify.ui.theme.darkGrayishCyan
import com.guarantify.ui.theme.emeraldGreen
import com.guarantify.ui.theme.goldenYellow
import com.guarantify.warranties.model.WarrantyStatus

fun WarrantyStatus.toColor(): Color {
    return when (this) {
        WarrantyStatus.LONG_TERM -> emeraldGreen
        WarrantyStatus.WARNING -> goldenYellow
        WarrantyStatus.CRITICAL -> burntOrange
        WarrantyStatus.EXPIRED -> darkGrayishCyan
    }
}