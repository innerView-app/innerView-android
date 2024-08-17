package com.dev.innerview.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme

@Composable
fun InnerViewDetailScreen(
    id: Int,
    onBackClick: () -> Unit,
    onInterviewGroupClick: () -> Unit,
    onInnerViewQuestionClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = id.toString(),
            navigationType = TopAppBarNavigationType.Back,
            onNavigationClick = { onBackClick() },
            actionButtons = {
                InnerViewAppBarIcon(
                    imageVector = Icons.Filled.List,
                    navigationIconContentDescription = null,
                    onClick = { onInnerViewQuestionClick() }
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
        Box(
            modifier = Modifier
                .systemBarsPadding()
                .padding(top = appBarSize)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
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
            id = 0,
            onBackClick = {},
            onInterviewGroupClick = {},
            onInnerViewQuestionClick = {}
        )
    }
}
