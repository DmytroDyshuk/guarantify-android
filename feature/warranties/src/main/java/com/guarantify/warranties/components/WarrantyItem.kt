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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guarantify.ui.theme.GuarantifyTheme
import com.guarantify.warranties.model.WarrantyUiModel

@Composable
fun WarrantyItem(
    modifier: Modifier = Modifier,
    warranty: WarrantyUiModel
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = warranty.title,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W700)
            )
            Spacer(Modifier.weight(weight = 1f))
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = warranty.remainingDays,
                style = MaterialTheme.typography.labelLarge
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = warranty.status, shape = CircleShape)
            )
        }
        warranty.brand?.let { brand ->
            Text(
                text = "Brand: $brand",
                style = MaterialTheme.typography.labelSmall
            )
        }
        warranty.storeName?.let { storeName ->
            Text(
                text = "Store: $storeName",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W600)
            )
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = warranty.formattedExpirationDate,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600)
        )
        Text(
            text = warranty.formattedPurchaseDate,
            style = MaterialTheme.typography.labelMedium
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 2.dp
        )
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