package com.dev.innerview.core.designsystem.component

import android.R
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.theme.InnerViewTheme

val appBarSize = 48.dp

@Composable
fun InnerViewAppBarIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    navigationIconContentDescription: String?,
    onNavigationClick: () -> Unit = {}
) {
    IconButton(
        onClick = onNavigationClick,
        modifier = modifier.size(appBarSize)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = navigationIconContentDescription,
        )
    }
}

@Composable
fun InnerViewTopAppBar(
    @StringRes titleRes: Int? = null,
    titleString: String = "",
    navigationIconContentDescription: String? = null,
    navigationType: TopAppBarNavigationType = TopAppBarNavigationType.None,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    actionButtons: @Composable () -> Unit = {},
    onNavigationClick: () -> Unit = {},
) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(appBarSize)
                .background(containerColor)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (navigationType == TopAppBarNavigationType.Back) {
                    InnerViewAppBarIcon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        navigationIconContentDescription = navigationIconContentDescription,
                        onNavigationClick = onNavigationClick
                    )
                } else {
                    Spacer(modifier = Modifier.size(appBarSize))
                }
                Text(
                    text = titleRes?.let { stringResource(id = titleRes) } ?: titleString,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actionButtons()
            }
        }
    }
}

enum class TopAppBarNavigationType { Back, None }

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun InnerViewTopAppBarPreviewBack() {
    InnerViewTheme {
        InnerViewTopAppBar(
            titleRes = R.string.untitled,
            navigationType = TopAppBarNavigationType.Back,
            navigationIconContentDescription = "Navigation icon"
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun InnerViewTopAppBarPreviewNone() {
    InnerViewTheme {
        InnerViewTopAppBar(
            titleRes = R.string.untitled,
            navigationType = TopAppBarNavigationType.None,
            navigationIconContentDescription = "Navigation icon"
        )
    }
}