package com.dev.innerview.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.component.InnerViewCard
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
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

@Composable
fun Greeting() {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        topBar = {
            InnerViewTopAppBar(
                titleString = "innerView",
                navigationType = TopAppBarNavigationType.Back
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(Paddings.large)
        ) {
            InnerViewCard(
                modifier = Modifier.height(120.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Billie Eilish",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = Paddings.large,
                                end = Paddings.large,
                                top = Paddings.large
                            )
                    )

                    Text(
                        text = "2017.10.18 ~ D+2443",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(
                                start = Paddings.large,
                                end = Paddings.large,
                                bottom = Paddings.large
                            )
                    )

                    Text(
                        text = "1 year",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(
                                start = Paddings.large,
                                end = Paddings.large,
                                bottom = Paddings.large
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InnerViewTheme {
        Greeting()
    }
}