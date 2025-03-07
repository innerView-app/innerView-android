package com.dev.innerview.feature.edit.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.component.VideoThumbnail
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.feature.edit.model.InnerViewSelectUiState
import com.dev.innerview.feature.edit.model.InterviewGroupSelectUiState
import com.dev.innerview.feature.edit.model.InterviewSelectUiState
import com.dev.innerview.feature.edit.model.MediaAddUiState
import kotlinx.collections.immutable.persistentListOf
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaAddBottomSheet(
    modifier: Modifier = Modifier,
    mediaAddUiState: MediaAddUiState,
    sheetState: SheetState = rememberModalBottomSheetState(),
    closeSheet: () -> Unit,
    selectInnerViewItem: (String) -> Unit,
    selectInterviewGroupItem: (String, Int) -> Unit,
    selectInterviewItem: (String, Int, Int) -> Unit,
    addInterviewItem: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = closeSheet,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.xlarge, vertical = Paddings.extra)
        ) {
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (mediaAddUiState.selectedInnerProjectId.isNotEmpty()) {
                    Text(
                        modifier = Modifier.padding(end = Paddings.large),
                        text = "선택된 인터뷰 ${mediaAddUiState.selectedInnerProjectId.size}개",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 30.dp)
                        .clickable {
                            addInterviewItem()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier,
                        text = "추가",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                mediaAddUiState.innerViews.forEach { innerViewSelectUiState ->
                    item {
                        InnerViewSelectItem(
                            innerViewSelectUiState = innerViewSelectUiState,
                            selectInnerViewItem = selectInnerViewItem
                        )
                    }

                    if (innerViewSelectUiState.isOpen) {
                        innerViewSelectUiState.innerViewContents.forEach { interviewGroupSelectUiState ->
                            item {
                                InterviewGroupSelectItem(
                                    interviewGroupSelectUiState = interviewGroupSelectUiState,
                                    selectInterviewGroupItem = selectInterviewGroupItem
                                )
                            }

                            if (interviewGroupSelectUiState.isOpen) {
                                interviewGroupSelectUiState.interviewContents.forEach { interviewSelectUiState ->
                                    item {
                                        InterviewSelectItem(
                                            interviewSelectUiState = interviewSelectUiState,
                                            selectInterviewItem = selectInterviewItem
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InnerViewSelectItem(
    innerViewSelectUiState: InnerViewSelectUiState,
    selectInnerViewItem: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectInnerViewItem(innerViewSelectUiState.innerViewId)
            }
            .padding(all = Paddings.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (innerViewSelectUiState.isOpen) {
                Icons.Filled.KeyboardArrowDown
            } else {
                Icons.Filled.ChevronRight
            },
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(start = Paddings.medium),
            text = innerViewSelectUiState.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    HorizontalDivider()
}

@Composable
private fun InterviewGroupSelectItem(
    interviewGroupSelectUiState: InterviewGroupSelectUiState,
    selectInterviewGroupItem: (String, Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectInterviewGroupItem(
                    interviewGroupSelectUiState.innerViewId,
                    interviewGroupSelectUiState.interviewGroupId
                )
            }
            .padding(all = Paddings.medium)
            .padding(start = Paddings.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (interviewGroupSelectUiState.isOpen) {
                Icons.Filled.KeyboardArrowDown
            } else {
                Icons.Filled.ChevronRight
            },
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(start = Paddings.medium),
            text = interviewGroupSelectUiState.createdAt.withZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    HorizontalDivider()
}

@Composable
private fun InterviewSelectItem(
    interviewSelectUiState: InterviewSelectUiState,
    selectInterviewItem: (String, Int, Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectInterviewItem(
                    interviewSelectUiState.innerViewId,
                    interviewSelectUiState.interviewGroupId,
                    interviewSelectUiState.innerProjectId
                )
            }
            .padding(start = Paddings.medium * 3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VideoThumbnail(
            modifier = Modifier.size(35.dp),
            filePath = interviewSelectUiState.thumbnailVideoPath ?: ""
        )

        Text(
            modifier = Modifier
                .padding(start = Paddings.medium)
                .weight(1f),
            text = interviewSelectUiState.question,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Checkbox(
            checked = interviewSelectUiState.selected,
            onCheckedChange = {
                selectInterviewItem(
                    interviewSelectUiState.innerViewId,
                    interviewSelectUiState.interviewGroupId,
                    interviewSelectUiState.innerProjectId
                )
            },
        )
    }
    HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun MediaAddBottomSheetBottomSheetPreview() {
    InnerViewTheme {
        val sheetState = rememberStandardBottomSheetState()

        MediaAddBottomSheet(
            modifier = Modifier,
            mediaAddUiState = MediaAddUiState(
                innerViews = persistentListOf(
                    InnerViewSelectUiState(
                        innerViewId = "1",
                        title = "innerView 1",
                        isOpen = true,
                        innerViewContents = persistentListOf(
                            InterviewGroupSelectUiState(
                                interviewGroupId = 1,
                                isOpen = true,
                                interviewContents = persistentListOf(
                                    InterviewSelectUiState(
                                        innerProjectId = 1,
                                        question = "question 1",
                                        selected = true
                                    ),
                                    InterviewSelectUiState(
                                        innerProjectId = 2,
                                        question = "question 2",
                                        selected = false
                                    ),
                                    InterviewSelectUiState(
                                        innerProjectId = 3,
                                        question = "question 3",
                                        selected = false
                                    )
                                )
                            ),
                            InterviewGroupSelectUiState(
                                interviewGroupId = 2,
                                isOpen = false,
                                interviewContents = persistentListOf()
                            ),
                        )
                    ),
                    InnerViewSelectUiState(
                        innerViewId = "2",
                        title = "innerView 2",
                        isOpen = false,
                        innerViewContents = persistentListOf()
                    ),
                )
            ),
            sheetState = sheetState,
            closeSheet = {},
            selectInnerViewItem = {},
            selectInterviewGroupItem = { _, _ -> },
            selectInterviewItem = { _, _, _ -> },
            addInterviewItem = {},
        )
    }
}