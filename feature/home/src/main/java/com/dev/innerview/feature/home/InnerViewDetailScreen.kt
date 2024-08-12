package com.dev.innerview.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.theme.InnerViewTheme

@Composable
fun InnerViewDetailScreen(
    id: String,
    padding: PaddingValues,
    onBackClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        topBar = {
            InnerViewTopAppBar(
                titleString = id,
                navigationType = TopAppBarNavigationType.Back,
                onNavigationClick = { onBackClick() },
                actionButtons = {
                    InnerViewAppBarIcon(
                        imageVector = Icons.Filled.List,
                        navigationIconContentDescription = null
                    )
                    InnerViewAppBarIcon(
                        imageVector = Icons.Filled.Notifications,
                        navigationIconContentDescription = null
                    )
                    InnerViewAppBarIcon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        navigationIconContentDescription = null
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "innerViewDetail Screen",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InnerViewDetailScreenPreview() {
    InnerViewTheme {
        InnerViewDetailScreen(
            id = "test id",
            padding = PaddingValues(),
            onBackClick = {}
        )
    }
}
