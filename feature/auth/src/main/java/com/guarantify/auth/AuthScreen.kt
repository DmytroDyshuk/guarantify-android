package com.guarantify.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.guarantify.auth.components.BulletPointText
import com.guarantify.auth.components.ContinueWithGoogleButton
import com.guarantify.auth.state.AuthUiState
import com.guarantify.auth.viewmodel.AuthViewModel
import com.guarantify.ui.theme.AppTypography
import com.guarantify.ui.theme.GuarantifyTheme
import com.guarantify.ui.theme.displayFontFamily
import com.guarantify.ui.theme.primaryLight
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(authViewModel: AuthViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val googleAuthUiClient = remember { GoogleAuthUiClient(context) }
    val coroutineScope = rememberCoroutineScope()

    val uiState by authViewModel.uiState.collectAsState()

    AuthScreenContent(
        uiState = uiState,
        onGoogleButtonClick = {
            authViewModel.setLoading()
            coroutineScope.launch {
                try {
                    val idToken = googleAuthUiClient.getIdTokenCredential()
                    idToken?.let {
                        authViewModel.continueWithGoogle(it)
                    }
                } catch (e: Exception) {
                    when (e) {
                        is NoCredentialException -> {
                            Toast.makeText(
                                context,
                                "No Google accounts available",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is GetCredentialCancellationException -> {
                            Log.d("AuthScreen", "User cancelled Google sign-in")
                            Toast.makeText(
                                context,
                                "Sign-in cancelled",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Log.d("AuthScreen", "Exception: ${e.message}")
                            Toast.makeText(
                                context,
                                "Something went wrong, please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AuthScreenContent(
    uiState: AuthUiState,
    onGoogleButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(32.dp)
                        .padding(bottom = 48.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                ContinueWithGoogleButton(
                    modifier = Modifier.padding(bottom = 48.dp)
                ) {
                    onGoogleButtonClick()
                }
            }
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