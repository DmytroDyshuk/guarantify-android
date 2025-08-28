package com.guarantify.auth.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.guarantify.ui.theme.AppTypography

@Composable
fun BulletPointText(
    modifier: Modifier = Modifier,
    bulletColor: Color = Color.Black,
    textColor: Color = Color.Black,
    textStyle: TextStyle = AppTypography.bodyLarge,
    text: String
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = "•",
            color = bulletColor,
            fontSize = 20.sp,
            style = textStyle
        )
        Text(
            text = " $text",
            color = textColor,
            style = textStyle
        )
    }
}