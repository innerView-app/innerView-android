package com.dev.innerview.feature.peek

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun ProfileRoute(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    ProfileScreen(
        padding = padding,
    )
}

@Composable
private fun ProfileScreen(
    padding: PaddingValues,
) {
    Scaffold(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        topBar = {
            InnerViewTopAppBar(
                titleString = "프로필",
                navigationType = TopAppBarNavigationType.None,
                actionButtons = {
                    InnerViewAppBarIcon(
                        imageVector = Icons.Filled.Settings,
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
                text = "Profile Screen",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ProfileScreenPreview() {
    InnerViewTheme {
        ProfileScreen(padding = PaddingValues())
    }
}