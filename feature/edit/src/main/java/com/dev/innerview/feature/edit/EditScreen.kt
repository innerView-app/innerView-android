package com.dev.innerview.feature.edit

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun EditRoute(
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onBackClick: () -> Unit,
    viewModel: EditViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    EditScreen(
        onBackClick = onBackClick
    )
}

@Composable
private fun EditScreen(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = "편집",
            navigationType = TopAppBarNavigationType.Back,
            onNavigationClick = { onBackClick() },
            actionButtons = {
                InnerViewAppBarIcon(
                    imageVector = Icons.Filled.Done,
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
                text = "Edit Screen",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun EditScreenPreview() {
    InnerViewTheme {
        EditScreen(
            onBackClick = {}
        )
    }
}
