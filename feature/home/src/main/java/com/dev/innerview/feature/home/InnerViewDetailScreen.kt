package com.dev.innerview.feature.home

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewFloatingActionButton
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.InterviewGroupCard
import com.dev.innerview.core.designsystem.component.OutlinedText
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InterviewGroup
import com.dev.innerview.core.model.RecordState
import com.dev.innerview.feature.home.model.InnerViewDetailUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun InnerViewDetailScreen(
    innerViewId: String,
    onBackClick: () -> Unit,
    navigateToInterviewGroup: () -> Unit,
    navigateToInnerViewQuestion: (String) -> Unit,
    navigateToRecord: (String, Int) -> Unit,
    viewModel: InnerViewDetailViewModel = hiltViewModel()
) {

    val innerViewDetailUiState by viewModel.innerViewDetailUiState.collectAsStateWithLifecycle()

    LaunchedEffect(innerViewId) {
        viewModel.fetchInnerView(innerViewId)
    }

    InnerViewDetailContent(
        innerViewId = innerViewId,
        innerViewDetailUiState = innerViewDetailUiState,
        onBackClick = onBackClick,
        navigateToInterviewGroup = navigateToInterviewGroup,
        navigateToInnerViewQuestion = navigateToInnerViewQuestion,
        navigateToRecord = navigateToRecord,
        addInterviewGroup = { viewModel.addInnerViewGroup(innerViewId) }
    )
}

@Composable
private fun InnerViewDetailContent(
    innerViewId: String,
    innerViewDetailUiState: InnerViewDetailUiState,
    onBackClick: () -> Unit,
    navigateToInnerViewQuestion: (String) -> Unit,
    navigateToInterviewGroup: () -> Unit,
    navigateToRecord: (String, Int) -> Unit,
    addInterviewGroup: () -> Unit
) {
    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = innerViewDetailUiState.title,
            navigationType = TopAppBarNavigationType.Back,
            onNavigationClick = { onBackClick() },
            actionButtons = {
                InnerViewAppBarIcon(
                    imageVector = Icons.Filled.List,
                    navigationIconContentDescription = null,
                    onClick = { navigateToInnerViewQuestion(innerViewId) }
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
            InterviewGroupList(
                innerViewId = innerViewId,
                interviewGroups = innerViewDetailUiState.interviewGroups,
                navigateToInterviewGroup = navigateToInterviewGroup,
                navigateToRecord = navigateToRecord,
                reactivateDate = innerViewDetailUiState.reactivateAt,
                isActivated = innerViewDetailUiState.isActivated
            )

            //if(innerViewDetailUiState.isActivated) {
            InnerViewFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Paddings.large, bottom = Paddings.large),
                iconImageVector = Icons.Filled.Add,
                text = "인터뷰 시작",
                onClick = { addInterviewGroup() }
            )
            //}
        }
    }
}

@Composable
private fun InterviewGroupList(
    innerViewId: String,
    interviewGroups: ImmutableList<InterviewGroup>,
    navigateToInterviewGroup: () -> Unit,
    navigateToRecord: (String, Int) -> Unit,
    reactivateDate: LocalDate,
    isActivated: Boolean,
) {
    val lazyGridState = rememberLazyGridState()
    val lastScrolledForward by remember {
        derivedStateOf {
            lazyGridState.lastScrolledForward
        }
    }

    val animatedHeight by animateDpAsState(
        targetValue = if (lastScrolledForward) 0.dp else 60.dp,
        label = "collapsing animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Paddings.large)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
        ) {
            val reactivateAt = reactivateDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = if (isActivated) "새로운 인터뷰를 촬영할 수 있습니다."
                else "$reactivateAt 에 새로운 인터뷰를 촬영할 수 있습니다.",
                style = MaterialTheme.typography.labelMedium
            )
        }

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            state = lazyGridState,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(Paddings.medium),
            horizontalArrangement = Arrangement.spacedBy(Paddings.medium),
        ) {
            items(interviewGroups, key = { it.id }) {

                val createAt = it.createdAt.withZoneSameInstant(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

                InterviewGroupCard(
                    modifier = Modifier
                        .height(240.dp)
                        .clickable {
                            when (it.recordState) {
                                RecordState.RECODING -> navigateToRecord(innerViewId, it.id)

                                RecordState.COMPLETE -> navigateToInterviewGroup()

                                else -> {}
                            }
                        },
                    filePath = it.thumbnailVideoPath
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = Paddings.medium, bottom = Paddings.medium)
                    ) {
                        OutlinedText(
                            text = "${it.questionCount}개의 질문",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onTertiary
                            ),
                            outlineColor = MaterialTheme.colorScheme.tertiary,
                            outlineDrawStyle = Stroke(
                                width = 4f
                            )
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        OutlinedText(
                            text = createAt,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onTertiary
                            ),
                            outlineColor = MaterialTheme.colorScheme.tertiary,
                            outlineDrawStyle = Stroke(
                                width = 5f
                            )
                        )
                    }
                }
            }
            repeat(interviewGroups.size % 2 + 1) {
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InnerViewDetailContentPreview() {
    InnerViewTheme {
        InnerViewDetailContent(
            innerViewId = "",
            innerViewDetailUiState = InnerViewDetailUiState(
                title = "title",
                interviewGroups = persistentListOf(
                    InterviewGroup(
                        id = 1,
                        createdAt = ZonedDateTime.now(),
                        recordState = RecordState.RECODING,
                        questionCount = 30,
                        thumbnailVideoPath = null
                    ),
                    InterviewGroup(
                        id = 2,
                        createdAt = ZonedDateTime.now(),
                        recordState = RecordState.RECODING,
                        questionCount = 20,
                        thumbnailVideoPath = null
                    ),
                    InterviewGroup(
                        id = 3,
                        createdAt = ZonedDateTime.now(),
                        recordState = RecordState.RECODING,
                        questionCount = 10,
                        thumbnailVideoPath = null
                    )
                )
            ),
            onBackClick = {},
            navigateToInterviewGroup = {},
            navigateToInnerViewQuestion = {},
            navigateToRecord = { _, _ -> },
            addInterviewGroup = {}
        )
    }
}
