package com.dev.innerview.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewFloatingActionButton
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.feature.home.model.HomeUiState
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun HomeRoute(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onInnerViewClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {

    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    HomeScreen(
        homeUiState = homeUiState,
        padding = padding,
        onInnerViewClick = onInnerViewClick
    )
}


@Composable
private fun HomeScreen(
    homeUiState: HomeUiState,
    padding: PaddingValues,
    onInnerViewClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = stringResource(id = R.string.feature_home_innerview_screen_title),
            navigationType = TopAppBarNavigationType.None,
            actionButtons = {
                InnerViewAppBarIcon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_upload),
                    navigationIconContentDescription = stringResource(R.string.feature_home_icon_description_upload)
                )
            }
        )

        Box(
            modifier = Modifier
                .padding(top = appBarSize)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (homeUiState) {
                is HomeUiState.Loading -> Loading()
                is HomeUiState.UiState -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                    }
                    InnerViewFloatingActionButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = Paddings.large, bottom = Paddings.large),
                        iconImageVector = Icons.Filled.Add,
                        text = stringResource(R.string.feature_home_innerview_create),
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun HomeScreenPreview() {
    InnerViewTheme {
        HomeScreen(
            homeUiState = HomeUiState.UiState(),
            padding = PaddingValues(),
            onInnerViewClick = {}
        )
    }
}
