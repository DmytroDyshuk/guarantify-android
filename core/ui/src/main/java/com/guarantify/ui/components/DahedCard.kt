package com.guarantify.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashedCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    strokeWidth: Dp = 2.dp,
    dashWidth: Dp = 8.dp,
    dashGap: Dp = 6.dp,
    color: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .drawBehind {
                drawRoundRect(
                    color = color,
                    size = size,
                    style = Stroke(
                        width = strokeWidth.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(dashWidth.toPx(), dashGap.toPx())
                        )
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}