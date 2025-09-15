package com.guarantify.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.guarantify.app.navigation.AppNavGraph
import com.guarantify.ui.components.LoadingScreen
import com.guarantify.ui.theme.GuarantifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        setContent {
            GuarantifyTheme {
                val uiState by viewModel.uiState.collectAsState()

                when (uiState) {
                    is MainActivityUiState.Success -> {
                        (uiState as? MainActivityUiState.Success)?.authState?.let {
                            AppNavGraph(authState = it)
                        }
                    }

                    MainActivityUiState.Loading -> {
                        LoadingScreen()
                    }
                }
            }
        }
    }
}