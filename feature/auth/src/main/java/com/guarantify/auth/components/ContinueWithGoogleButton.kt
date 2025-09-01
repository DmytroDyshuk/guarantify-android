package com.guarantify.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guarantify.auth.R
import com.guarantify.ui.theme.GuarantifyTheme
import com.guarantify.ui.theme.audiBrilliantBlack
import com.guarantify.ui.theme.darkGrayishCyan

@Composable
fun ContinueWithGoogleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = audiBrilliantBlack
        ),
        border = BorderStroke(1.dp, darkGrayishCyan),
        onClick = { onClick }
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = 10.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_google_logo),
            contentDescription = stringResource(R.string.content_description_google_logo),
            tint = Color.Unspecified
        )

        Text(
            text = stringResource(R.string.continue_with_google),
            style = MaterialTheme.typography.labelLarge,
            color = audiBrilliantBlack
        )
    }
}

@Preview
@Composable
fun ContinueWithGoogleButtonPreview() {
    GuarantifyTheme {
        ContinueWithGoogleButton {  }
    }
}