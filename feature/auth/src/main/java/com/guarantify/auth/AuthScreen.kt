package com.guarantify.auth

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guarantify.auth.components.BulletPointText
import com.guarantify.auth.components.ContinueWithGoogleButton
import com.guarantify.ui.theme.AppTypography
import com.guarantify.ui.theme.GuarantifyTheme
import com.guarantify.ui.theme.displayFontFamily
import com.guarantify.ui.theme.primaryLight

@Composable
fun AuthScreen() {
    AuthScreenContent()
}

@Composable
fun AuthScreenContent() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            textAlign = TextAlign.Center,
            style = AppTypography.displayMedium,
            fontWeight = FontWeight.SemiBold,
            text = buildAnnotatedString {
                append(stringResource(R.string.auth_welcome_title))
                append(" ")
                withStyle(
                    style = SpanStyle(
                        fontFamily = displayFontFamily,
                        fontSize = AppTypography.displayMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = primaryLight
                    )
                ) {
                    append(stringResource(R.string.aut_guarantify_title))
                }
            }
        )

        Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 8.dp)) {
            Text(
                text = stringResource(R.string.auth_description_header),
                color = Color.Black,
                style = AppTypography.bodyMedium
            )
            BulletPointText(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(R.string.auth_description_bullet_point_1)
            )
            BulletPointText(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(R.string.auth_description_bullet_point_2)
            )
            BulletPointText(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(R.string.auth_description_bullet_point_3)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.auth_description_footer),
                color = Color.Black,
                style = AppTypography.bodyMedium
            )
        }


        Image(
            modifier = Modifier.padding(top = 24.dp),
            painter = painterResource(R.drawable.auth_flat_design_picture),
            contentDescription = null
        )

        Spacer(modifier = Modifier.weight(1f))

        ContinueWithGoogleButton(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

        }

    }
}


@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    GuarantifyTheme {
        AuthScreen()
    }
}