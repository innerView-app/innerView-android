package com.dev.innerview.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navigator: MainNavigator = rememberMainNavigator()

            InnerViewTheme {
                MainScreen(
                    navigator = navigator
                )
            }
        }
    }
}