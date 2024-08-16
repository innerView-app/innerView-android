package com.dev.innerview.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun HomeRoute(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onInnerViewClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    HomeScreen(
        padding = padding,
        onInnerViewClick = onInnerViewClick
    )
}

@Composable
private fun HomeScreen(
    padding: PaddingValues,
    onInnerViewClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            titleString = stringResource(R.string.feature_home_innerview_screen_title),
            navigationType = TopAppBarNavigationType.None,
            actionButtons = {
                InnerViewAppBarIcon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_upload),
                    navigationIconContentDescription = stringResource(R.string.feature_home_icon_description_upload)
                )
            }
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun HomeScreenPreview() {
    InnerViewTheme {
        HomeScreen(padding = PaddingValues(), onInnerViewClick = {})
    }
}
