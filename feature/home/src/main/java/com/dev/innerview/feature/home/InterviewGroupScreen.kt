package com.dev.innerview.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewFloatingActionButton
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.InterviewCard
import com.dev.innerview.core.designsystem.component.OutlinedText
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.Interview
import com.dev.innerview.feature.home.model.InterviewGroupUiState
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun InterviewGroupScreen(
    padding: PaddingValues,
    innerViewId: String,
    interviewGroupId: Int,
    onBackClick: () -> Unit,
    navigateToPlayer: () -> Unit,
    viewModel: InterviewGroupViewModel = hiltViewModel()
) {
    val uiState by viewModel.interviewGroupUiState.collectAsStateWithLifecycle()

    LaunchedEffect(innerViewId) {
        viewModel.fetchInterviewGroup(innerViewId, interviewGroupId)
    }

    InterviewGroupContent(
        uiState = uiState,
        padding = padding,
        onBackClick = onBackClick,
        navigateToPlayer = navigateToPlayer
    )
}

@Composable
private fun InterviewGroupContent(
    padding: PaddingValues,
    uiState: InterviewGroupUiState,
    navigateToPlayer: () -> Unit,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = uiState.title,
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
            LazyColumn(
                modifier = Modifier
                    .padding(Paddings.large)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Paddings.large)
            ) {
                items(uiState.interviews, key = { it.question }) { interview ->
                    InterviewCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clickable {
                                navigateToPlayer()
                                // TODO("인터뷰 재생")
                            },
                        interviewDescription = interview.question,
                        filePath = interview.thumbnailVideoPath
                    ) {
                        OutlinedText(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxWidth()
                                .padding(horizontal = Paddings.large),
                            text = interview.question,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onTertiary
                            ),
                            outlineColor = MaterialTheme.colorScheme.tertiary,
                            outlineDrawStyle = Stroke(width = 5f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
            InnerViewFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Paddings.large),
                iconImageVector = Icons.Filled.PlayArrow,
                text = stringResource(R.string.feature_home_interview_group_play_all),
                onClick = {
                    navigateToPlayer()
                    // TODO("인터뷰 그룹 전체 재생")
                }
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InterviewGroupScreenPreview() {
    InnerViewTheme {
        InterviewGroupContent(
            uiState = InterviewGroupUiState(
                title = "title",
                interviews = persistentListOf(
                    Interview(question = "question 1"),
                    Interview(question = "question 2"),
                    Interview(question = "question 3")
                )
            ),
            padding = PaddingValues(),
            onBackClick = {},
            navigateToPlayer = {}
        )
    }
}
