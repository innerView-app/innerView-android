package com.dev.innerview.feature.record.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class RecordUiState(
    val interviews: ImmutableList<InterviewItemUiState> = persistentListOf(),
    val isQuestionAddDialogVisible: Boolean = false,
    val selectableQuestions: ImmutableList<String> = persistentListOf("", "", "", ""),
    val selectedQuestion: Int = 0
)