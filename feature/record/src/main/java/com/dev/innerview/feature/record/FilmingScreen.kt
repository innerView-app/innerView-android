package com.dev.innerview.feature.record

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.innerview.core.designsystem.component.InnerViewFloatingActionButton
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun FilmingScreen(
    innerViewId: String,
    interviewGroupId: Int,
    question: String,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    viewModel: FilmingViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    FilmingContent(
        question = question,
        padding = padding,
        onBackClick = onBackClick,
        addInnerProject = { viewModel.addInnerProject(innerViewId, interviewGroupId, question) }
    )
}

@Composable
private fun FilmingContent(
    question: String,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    addInnerProject: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = question,
            navigationType = TopAppBarNavigationType.Back,
            onNavigationClick = onBackClick,
        )
        Box(
            modifier = Modifier
                .padding(top = appBarSize)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {

            InnerViewFloatingActionButton(
                iconImageVector = Icons.Filled.Add,
                onClick = addInnerProject
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun FilmingContentPreview() {
    InnerViewTheme {
        FilmingContent(
            question = "question",
            padding = PaddingValues(),
            onBackClick = {},
            addInnerProject = {}
        )
    }
}