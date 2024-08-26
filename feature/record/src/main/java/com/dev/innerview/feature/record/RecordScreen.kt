package com.dev.innerview.feature.record

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewCard
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.InterviewCard
import com.dev.innerview.core.designsystem.component.OutlinedText
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.Interview
import com.dev.innerview.feature.record.component.QuestionAddDialog
import com.dev.innerview.feature.record.model.RecordUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import java.time.ZonedDateTime

@Composable
internal fun RecordScreen(
    innerViewId: String,
    interviewGroupId: Int,
    title: String,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    viewModel: RecordViewModel = hiltViewModel()
) {

    val recordUiState by viewModel.recordUiState.collectAsStateWithLifecycle()

    val localContextResource = LocalContext.current.resources

    LaunchedEffect(true) {
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

    LaunchedEffect(innerViewId, interviewGroupId, title) {
        viewModel.fetchInnerView(innerViewId, interviewGroupId)
    }

    RecordContent(
        innerViewId = innerViewId,
        interviewGroupId = interviewGroupId,
        title = title,
        recordUiState = recordUiState,
        padding = padding,
        onBackClick = onBackClick,
        selectQuestionAdd = { viewModel.selectQuestionAdd() },
        updateCustomQuestion = { viewModel.updateCustomQuestion(it) },
        updateDialogSelectedType = { viewModel.updateDialogSelectedType(it) },
        addQuestion = { viewModel.addQuestion(innerViewId, interviewGroupId) }
    )
}

@Composable
private fun RecordContent(
    innerViewId: String,
    interviewGroupId: Int,
    title: String,
    recordUiState: RecordUiState,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    selectQuestionAdd: () -> Unit,
    updateCustomQuestion: (String) -> Unit,
    updateDialogSelectedType: (Int) -> Unit,
    addQuestion: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {

        InnerViewTopAppBar(
            title = title,
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
                innerViewId = innerViewId,
                interviewGroupId = interviewGroupId,
                interviews = recordUiState.interviews,
                selectQuestionAdd = selectQuestionAdd
            )
        }

        if (recordUiState.isQuestionAddDialogVisible) {
            QuestionAddDialog(
                recordUiState = recordUiState,
                onCustomQuestionChange = updateCustomQuestion,
                onSelectType = updateDialogSelectedType,
                onDismissRequest = { selectQuestionAdd() },
                onConfirmRequest = { addQuestion() }
            )
        }
    }
}

@Composable
private fun InterviewList(
    innerViewId: String,
    interviewGroupId: Int,
    interviews: ImmutableList<Interview>,
    selectQuestionAdd: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(Paddings.large)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Paddings.large)
    ) {

        items(interviews, key = { it.question }) {
            InterviewCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                filePath = it.thumbnailVideoPath
            ) {
                OutlinedText(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth()
                        .padding(horizontal = Paddings.large),
                    text = it.question,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onTertiary
                    ),
                    outlineColor = MaterialTheme.colorScheme.tertiary,
                    outlineDrawStyle = Stroke(
                        width = 5f
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        item {
            InnerViewCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable { selectQuestionAdd() },
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun RecordContentPreview() {
    InnerViewTheme {
        RecordContent(
            innerViewId = "innerViewId",
            interviewGroupId = 0,
            title = "title",
            recordUiState = RecordUiState(
                interviews = persistentListOf(
                    Interview(
                        createdAt = ZonedDateTime.now(),
                        question = "question 1",
                        isRequired = true,
                        isRecordComplete = false,
                        thumbnailVideoPath = null
                    ),
                    Interview(
                        createdAt = ZonedDateTime.now(),
                        question = "question 2",
                        isRequired = true,
                        isRecordComplete = false,
                        thumbnailVideoPath = null
                    ),
                    Interview(
                        createdAt = ZonedDateTime.now(),
                        question = "question 3",
                        isRequired = true,
                        isRecordComplete = false,
                        thumbnailVideoPath = null
                    )
                )
            ),
            padding = PaddingValues(),
            onBackClick = {},
            selectQuestionAdd = {},
            updateCustomQuestion = {},
            updateDialogSelectedType = {},
            addQuestion = {}
        )
    }
}