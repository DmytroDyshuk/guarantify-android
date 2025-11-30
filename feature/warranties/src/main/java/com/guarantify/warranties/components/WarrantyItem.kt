package com.guarantify.warranties.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guarantify.ui.theme.GuarantifyTheme
import com.guarantify.warranties.model.WarrantyUiModel

@Composable
fun WarrantyItem(
    modifier: Modifier = Modifier,
    warranty: WarrantyUiModel
) {
    val subtitleText = listOfNotNull(warranty.brand, warranty.storeName)
        .joinToString(" • ")

    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = warranty.title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W700),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.weight(weight = 1f))
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = warranty.remainingDays,
                    style = MaterialTheme.typography.labelSmall
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color = warranty.status, shape = CircleShape)
                )
            }
            if (subtitleText.isNotEmpty()) {
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = warranty.formattedExpirationDate,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600)
            )
            Text(
                text = warranty.formattedPurchaseDate,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun PreviewWarrantyItem() {
    GuarantifyTheme {
        Surface {
            WarrantyItem(
                warranty = WarrantyUiModel(
                    id = "123",
                    title = "Samsung Galaxy S23 Ultra",
                    formattedPurchaseDate = "Purchased: October 16, 2023",
                    formattedExpirationDate = "Valid until: October 15, 2025",
                    storeName = "",
                    brand = "Samsung",
                    remainingDays = "Expires today",
                    status = Color.Red
                )
            )
        }
    }
}