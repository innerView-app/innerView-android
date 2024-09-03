package com.dev.innerview.feature.record.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.Interview

@Immutable
data class InterviewItemUiState(
    val interview: Interview = Interview(),
    val isDropdownMenuVisible: Boolean = false,
    val isQuestionDeleteDialogVisible: Boolean = false,
    val isInnerProjectDeleteDialogVisible: Boolean = false
)