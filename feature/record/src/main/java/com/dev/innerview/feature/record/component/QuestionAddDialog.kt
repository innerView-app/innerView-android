package com.dev.innerview.feature.record.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.dev.innerview.core.designsystem.component.InnerViewDialog
import com.dev.innerview.core.designsystem.component.InnerViewDialogTextField
import com.dev.innerview.core.designsystem.component.InnerViewRadioButton
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.feature.record.R
import com.dev.innerview.feature.record.model.RecordUiState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun QuestionAddDialog(
    recordUiState: RecordUiState,
    onCustomQuestionChange: (String) -> Unit,
    onSelectType: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    onRefreshQuestion: () -> Unit
) {
    InnerViewDialog(
        titleText = stringResource(R.string.feature_record_question_add_dialog_title),
        contentText = stringResource(R.string.feature_record_question_add_dialog_content),
        confirmText = stringResource(R.string.feature_record_dialog_add_confirm),
        dismissText = stringResource(R.string.feature_record_dialog_dismiss),
        onDismissRequest = { onDismissRequest() },
        onConfirmRequest = { onConfirmRequest() },
        boxContent = {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = Paddings.medium, end = Paddings.large),
                onClick = { onRefreshQuestion() }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_refresh),
                    contentDescription = stringResource(R.string.feature_record_refresh_icon_description)
                )
            }
        }
    ) {
        recordUiState.selectableQuestions.forEachIndexed { i, question ->
            InnerViewRadioButton(
                selected = recordUiState.selectedQuestion == i,
                onClick = { onSelectType(i) }
            ) {
                if (i != recordUiState.selectableQuestions.size - 1) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Paddings.small),
                        text = question,
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else {
                    InnerViewDialogTextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Paddings.small),
                        value = { recordUiState.selectableQuestions.last() },
                        onValueChange = { onCustomQuestionChange(it) },
                        singleLine = false,
                        enabled = recordUiState.selectedQuestion == i,
                        placeholderText = stringResource(R.string.feature_record_question_add_dialog_placeholder),
                    )
                }
            }
            if (recordUiState.selectableQuestions.size - 1 > i) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun QuestionAddDialogPreview() {
    InnerViewTheme {
        QuestionAddDialog(
            recordUiState = RecordUiState(
                selectableQuestions = persistentListOf(
                    "question 1",
                    "question 2",
                    "question 3",
                    ""
                ),
                selectedQuestion = 0
            ),
            onCustomQuestionChange = {},
            onSelectType = {},
            onDismissRequest = {},
            onConfirmRequest = {},
            onRefreshQuestion = {}
        )
    }
}