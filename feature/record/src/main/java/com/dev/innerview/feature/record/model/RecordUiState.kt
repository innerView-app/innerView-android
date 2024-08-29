package com.dev.innerview.feature.record.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.InnerViewType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class RecordUiState(
    val title: String = "",
    val type: InnerViewType = InnerViewType.YEAR,
    val interviews: ImmutableList<InterviewItemUiState> = persistentListOf(),
    val isQuestionAddDialogVisible: Boolean = false,
    val selectableQuestions: ImmutableList<String> = persistentListOf("", "", "", ""),
    val selectedQuestion: Int = 0
)