package com.dev.innerview.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewFloatingActionButton
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.feature.home.component.InnerViewCreateDialog
import com.dev.innerview.feature.home.component.InnerViewItem
import com.dev.innerview.feature.home.model.HomeUiState
import com.dev.innerview.feature.home.model.InnerViewItemUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun HomeRoute(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToInnerViewDetail: (String, String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    HomeScreen(
        homeUiState = homeUiState,
        padding = padding,
        navigateToInnerViewDetail = navigateToInnerViewDetail,
        onInnerViewAddRequest = { viewModel.addInnerView() },
        maxInnerViewTitleLength = viewModel.maxInnerViewTitleLength,
        updateDialogInnerViewTitle = { viewModel.updateDialogInnerViewTitle(it) },
        updateDialogSelectedType = { viewModel.updateDialogSelectedType(it) },
        onInnerViewDeleteRequest = { viewModel.deleteInnerView(it) },
        onSelectInnerViewDropdown = { viewModel.selectInnerViewDropdown(it) },
        onSelectInnerViewCreate = { viewModel.selectInnerViewCreate() },
        onSelectInnerViewDelete = { viewModel.selectInnerViewDelete(it) }
    )
}


@Composable
private fun HomeScreen(
    homeUiState: HomeUiState,
    padding: PaddingValues,
    navigateToInnerViewDetail: (String, String) -> Unit,
    onInnerViewAddRequest: () -> Unit,
    onInnerViewDeleteRequest: (String) -> Unit,
    maxInnerViewTitleLength: Int,
    updateDialogInnerViewTitle: (String) -> Unit,
    updateDialogSelectedType: (InnerViewType) -> Unit,
    onSelectInnerViewDropdown: (String) -> Unit,
    onSelectInnerViewCreate: () -> Unit,
    onSelectInnerViewDelete: (String) -> Unit
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
            InnerViewList(
                innerViews = homeUiState.innerViews,
                onInnerViewClick = navigateToInnerViewDetail,
                onSelectInnerViewDropdown = onSelectInnerViewDropdown,
                onSelectInnerViewDelete = onSelectInnerViewDelete,
                onInnerViewDeleteRequest = onInnerViewDeleteRequest
            )
            InnerViewFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Paddings.large, bottom = Paddings.large),
                iconImageVector = Icons.Filled.Add,
                text = stringResource(R.string.feature_home_innerview_create),
                onClick = onSelectInnerViewCreate
            )
        }

        if (homeUiState.isInnerViewCreateDialogVisible) {
            InnerViewCreateDialog(
                homeUiState = homeUiState,
                maxInnerViewTitleLength = maxInnerViewTitleLength,
                onTitleChange = updateDialogInnerViewTitle,
                onSelectType = updateDialogSelectedType,
                onDismissRequest = onSelectInnerViewCreate,
                onConfirmRequest = onInnerViewAddRequest
            )
        }
    }
}

@Composable
private fun InnerViewList(
    innerViews: ImmutableList<InnerViewItemUiState>,
    onInnerViewClick: (String, String) -> Unit,
    onSelectInnerViewDropdown: (String) -> Unit,
    onSelectInnerViewDelete: (String) -> Unit,
    onInnerViewDeleteRequest: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Paddings.large),
        verticalArrangement = Arrangement.spacedBy(Paddings.large)
    ) {
        items(innerViews, key = { it.id }) { innerView ->
            InnerViewItem(
                innerViewItemState = innerView,
                onInnerViewClick = onInnerViewClick,
                onInnerViewLongClick = onSelectInnerViewDropdown,
                onSelectInnerViewDelete = onSelectInnerViewDelete,
                onInnerViewDeleteRequest = onInnerViewDeleteRequest
            )
        }
        item {
            Spacer(modifier = Modifier.size(80.dp))
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun HomeScreenPreview() {
    InnerViewTheme {
        HomeScreen(
            homeUiState = HomeUiState(
                innerViews = persistentListOf(
                    InnerViewItemUiState(
                        id = "1",
                        title = "innerView title 1"
                    ),
                    InnerViewItemUiState(
                        id = "2",
                        title = "innerView title 2"
                    )
                )
            ),
            padding = PaddingValues(),
            navigateToInnerViewDetail = { _, _ -> },
            onInnerViewAddRequest = {},
            maxInnerViewTitleLength = 0,
            updateDialogInnerViewTitle = {},
            updateDialogSelectedType = {},
            onInnerViewDeleteRequest = {},
            onSelectInnerViewDropdown = {},
            onSelectInnerViewCreate = {},
            onSelectInnerViewDelete = {}
        )
    }
}