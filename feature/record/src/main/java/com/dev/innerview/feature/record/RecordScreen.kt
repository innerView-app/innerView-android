package com.dev.innerview.feature.record

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewFloatingActionButton
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.Interview
import com.dev.innerview.feature.record.component.InterviewItem
import com.dev.innerview.feature.record.component.QuestionAddDialog
import com.dev.innerview.feature.record.model.InterviewItemUiState
import com.dev.innerview.feature.record.model.RecordUiEvent
import com.dev.innerview.feature.record.model.RecordUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun RecordScreen(
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigationToEdit: (Int) -> Unit,
    navigateToFilming: (String, Int, String) -> Unit,
    viewModel: RecordViewModel = hiltViewModel()
) {

    val recordUiState by viewModel.recordUiState.collectAsStateWithLifecycle()

    val localContextResource = LocalContext.current.resources

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collectLatest { throwable ->
            when (throwable) {
                is IllegalArgumentException -> {
                    onShowErrorSnackBar(
                        IllegalArgumentException(
                            localContextResource.getString(R.string.feature_record_exception_duplicate_question)
                        )
                    )
                }

                else -> {
                    onShowErrorSnackBar(throwable)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEventFlow.collectLatest { event ->
            when (event) {
                is RecordUiEvent.NavigateToBack -> {
                    onBackClick()
                }

                is RecordUiEvent.NavigateToFilming -> {
                    navigateToFilming(
                        recordUiState.innerViewId,
                        recordUiState.interviewGroupId,
                        event.question
                    )
                }
            }
        }
    }

    RecordContent(
        recordUiState = recordUiState,
        padding = padding,
        onBackClick = onBackClick,
        selectQuestionAdd = viewModel::selectQuestionAdd,
        updateCustomQuestion = viewModel::updateCustomQuestion,
        updateDialogSelectedType = viewModel::updateDialogSelectedType,
        addQuestion = viewModel::addQuestion,
        navigateToFilming = {
            navigateToFilming(
                recordUiState.innerViewId,
                recordUiState.interviewGroupId,
                it
            )
        },
        navigationToEdit = navigationToEdit,
        completeInterviewGroup = viewModel::completeInterviewGroup,
        onSelectInterviewDropdown = viewModel::selectInterviewDropdown,
        onSelectQuestionDelete = viewModel::selectQuestionDelete,
        onSelectInnerProjectDelete = viewModel::selectInnerProjectDelete,
        deleteQuestion = viewModel::deleteQuestion,
        deleteInnerProject = viewModel::deleteInnerProject,
        onRefreshQuestion = viewModel::updateRecommendQuestions
    )
}

@Composable
private fun RecordContent(
    recordUiState: RecordUiState,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    selectQuestionAdd: () -> Unit,
    updateCustomQuestion: (String) -> Unit,
    updateDialogSelectedType: (Int) -> Unit,
    addQuestion: () -> Unit,
    completeInterviewGroup: () -> Unit,
    navigateToFilming: (String) -> Unit,
    navigationToEdit: (Int) -> Unit,
    onSelectInterviewDropdown: (String) -> Unit,
    onSelectQuestionDelete: (String) -> Unit,
    onSelectInnerProjectDelete: (String) -> Unit,
    deleteQuestion: (String) -> Unit,
    deleteInnerProject: (String) -> Unit,
    onRefreshQuestion: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {

        InnerViewTopAppBar(
            title = recordUiState.title,
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

            InterviewList(
                innerViewType = recordUiState.type,
                interviews = recordUiState.interviews,
                navigateToFilming = navigateToFilming,
                navigationToEdit = navigationToEdit,
                onSelectInterviewDropdown = onSelectInterviewDropdown,
                onSelectQuestionDelete = onSelectQuestionDelete,
                onSelectInnerProjectDelete = onSelectInnerProjectDelete,
                deleteQuestion = deleteQuestion,
                deleteInnerProject = deleteInnerProject
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Paddings.large, bottom = Paddings.large),
                verticalArrangement = Arrangement.spacedBy(Paddings.large),
                horizontalAlignment = Alignment.End
            ) {
                InnerViewFloatingActionButton(
                    iconImageVector = Icons.Filled.Add,
                    text = stringResource(R.string.feature_record_add_question),
                    onClick = { selectQuestionAdd() }
                )
                AnimatedVisibility(
                    visible = recordUiState.interviews.isNotEmpty() && recordUiState.interviews.all { it.interview.isRecordComplete },
                    enter = slideIn { IntOffset(0, it.height) },
                    exit = slideOut { IntOffset(0, it.height) }
                ) {
                    InnerViewFloatingActionButton(
                        iconImageVector = ImageVector.vectorResource(id = R.drawable.ic_archive),
                        text = stringResource(R.string.feature_record_interview_complete),
                        onClick = { completeInterviewGroup() }
                    )
                }
            }
        }

        if (recordUiState.isQuestionAddDialogVisible) {
            QuestionAddDialog(
                recordUiState = recordUiState,
                onCustomQuestionChange = updateCustomQuestion,
                onSelectType = updateDialogSelectedType,
                onDismissRequest = { selectQuestionAdd() },
                onConfirmRequest = { addQuestion() },
                onRefreshQuestion = onRefreshQuestion
            )
        }
    }
}

@Composable
private fun InterviewList(
    innerViewType: InnerViewType,
    interviews: ImmutableList<InterviewItemUiState>,
    navigateToFilming: (String) -> Unit,
    navigationToEdit: (Int) -> Unit,
    onSelectInterviewDropdown: (String) -> Unit,
    onSelectQuestionDelete: (String) -> Unit,
    onSelectInnerProjectDelete: (String) -> Unit,
    deleteQuestion: (String) -> Unit,
    deleteInnerProject: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(Paddings.large)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Paddings.large)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                val description = when (innerViewType) {
                    InnerViewType.DAY -> stringResource(R.string.feature_record_innerview_everyday_description)
                    else -> stringResource(R.string.feature_record_innerview_repeat_description)
                }

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = description,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium.copy(
                        lineHeight = 18.sp
                    )
                )
            }
        }

        items(interviews, key = { it.interview.question }) { interviewItemUiState ->
            InterviewItem(
                interviewItemUiState = interviewItemUiState,
                navigateToFilming = navigateToFilming,
                navigationToEdit = navigationToEdit,
                onSelectInterviewDropdown = onSelectInterviewDropdown,
                onSelectQuestionDelete = onSelectQuestionDelete,
                onSelectInnerProjectDelete = onSelectInnerProjectDelete,
                deleteQuestion = deleteQuestion,
                deleteInnerProject = deleteInnerProject
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun RecordContentPreview() {
    InnerViewTheme {
        RecordContent(
            recordUiState = RecordUiState(
                interviews = persistentListOf(
                    InterviewItemUiState(
                        interview = Interview(
                            question = "question 1",
                        )
                    ),
                    InterviewItemUiState(
                        interview = Interview(
                            question = "question 2",
                        )
                    ),
                    InterviewItemUiState(
                        interview = Interview(
                            question = "question 3",
                        )
                    )
                )
            ),
            padding = PaddingValues(),
            onBackClick = {},
            selectQuestionAdd = {},
            updateCustomQuestion = {},
            updateDialogSelectedType = {},
            addQuestion = {},
            completeInterviewGroup = {},
            navigateToFilming = {},
            navigationToEdit = {},
            onSelectInterviewDropdown = {},
            onSelectQuestionDelete = {},
            onSelectInnerProjectDelete = {},
            deleteQuestion = {},
            deleteInnerProject = {},
            onRefreshQuestion = {}
        )
    }
}