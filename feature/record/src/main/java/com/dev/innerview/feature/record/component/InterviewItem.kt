package com.dev.innerview.feature.record.component

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.component.InnerViewDialog
import com.dev.innerview.core.designsystem.component.InnerViewDropdownMenu
import com.dev.innerview.core.designsystem.component.InnerViewDropdownMenuItem
import com.dev.innerview.core.designsystem.component.InterviewCard
import com.dev.innerview.core.designsystem.component.OutlinedText
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.feature.record.R
import com.dev.innerview.feature.record.model.InterviewItemUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InterviewItem(
    interviewItemUiState: InterviewItemUiState,
    navigateToFilming: (String) -> Unit,
    onSelectInterviewDropdown: (String) -> Unit,
    onSelectQuestionDelete: (String) -> Unit,
    onSelectInnerProjectDelete: (String) -> Unit,
    deleteQuestion: (String) -> Unit,
    deleteInnerProject: (String) -> Unit,
) {
    InterviewCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .combinedClickable(
                onClick = { navigateToFilming(interviewItemUiState.interview.question) },
                onLongClick = { onSelectInterviewDropdown(interviewItemUiState.interview.question) }
            ),
        interviewDescription = interviewItemUiState.interview.question,
        filePath = interviewItemUiState.interview.thumbnailVideoPath
    ) {
        if (interviewItemUiState.interview.isRequired && !interviewItemUiState.interview.isRecordComplete) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = Paddings.medium, end = Paddings.medium),
                text = stringResource(R.string.feature_record_answer_required),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.error
                )
            )
        }

        OutlinedText(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .padding(horizontal = Paddings.large),
            text = interviewItemUiState.interview.question,
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

        InnerViewDropdownMenu(
            modifier = Modifier,
            expanded = interviewItemUiState.isDropdownMenuVisible,
            onDismissRequest = { onSelectInterviewDropdown(interviewItemUiState.interview.question) }
        ) {
            if (interviewItemUiState.interview.isRecordComplete) {
                InnerViewDropdownMenuItem(
                    text = stringResource(R.string.feature_record_innerproject_delete_dialog_title),
                    style = MaterialTheme.typography.labelLarge,
                    onClick = { onSelectInnerProjectDelete(interviewItemUiState.interview.question) },
                    onDismissRequest = { onSelectInterviewDropdown(interviewItemUiState.interview.question) }
                )
            }
            if (!interviewItemUiState.interview.isRequired) {
                InnerViewDropdownMenuItem(
                    text = stringResource(R.string.feature_record_question_delete_dialog_title),
                    style = MaterialTheme.typography.labelLarge,
                    onClick = { onSelectQuestionDelete(interviewItemUiState.interview.question) },
                    onDismissRequest = { onSelectInterviewDropdown(interviewItemUiState.interview.question) }
                )
            }
        }

        if (interviewItemUiState.isInnerProjectDeleteDialogVisible) {
            InnerViewDialog(
                titleText = stringResource(R.string.feature_record_innerproject_delete_dialog_title),
                contentText = stringResource(R.string.feature_record_innerproject_delete_dialog_content),
                confirmText = stringResource(R.string.feature_record_dialog_delete_confirm),
                dismissText = stringResource(R.string.feature_record_dialog_dismiss),
                onDismissRequest = { onSelectInnerProjectDelete(interviewItemUiState.interview.question) },
                onConfirmRequest = { deleteInnerProject(interviewItemUiState.interview.question) }
            )
        }

        if (interviewItemUiState.isQuestionDeleteDialogVisible) {
            InnerViewDialog(
                titleText = stringResource(R.string.feature_record_question_delete_dialog_title),
                contentText = stringResource(R.string.feature_record_question_delete_dialog_content),
                confirmText = stringResource(R.string.feature_record_dialog_delete_confirm),
                dismissText = stringResource(R.string.feature_record_dialog_dismiss),
                onDismissRequest = { onSelectQuestionDelete(interviewItemUiState.interview.question) },
                onConfirmRequest = { deleteQuestion(interviewItemUiState.interview.question) }
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InterviewItemPreview() {
    InnerViewTheme {
        InterviewItem(
            interviewItemUiState = InterviewItemUiState(
                isDropdownMenuVisible = false,
                isQuestionDeleteDialogVisible = false,
                isInnerProjectDeleteDialogVisible = false
            ),
            navigateToFilming = {},
            onSelectInterviewDropdown = {},
            onSelectQuestionDelete = {},
            onSelectInnerProjectDelete = {},
            deleteQuestion = {},
            deleteInnerProject = {},
        )
    }
}