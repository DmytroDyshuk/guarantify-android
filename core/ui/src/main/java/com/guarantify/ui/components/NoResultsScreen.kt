package com.guarantify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.guarantify.ui.R
import com.guarantify.ui.theme.GuarantifyTheme

@Composable
fun NoResultsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.no_results_error)
        )

        LottieAnimation(
            modifier = Modifier.size(350.dp),
            composition = composition,
            speed = 0.8f
        )
        Text(
            text = stringResource(R.string.core_ui_error_generic),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.core_ui_error_generic_description),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoResultsScreenPreview() {
    GuarantifyTheme {
        NoResultsScreen()
    }
}