package com.dev.innerview.feature.home.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class InnerViewQuestionUiState(
    val innerViewId: String = "",
    val title: String = "",
    val interviewQuestions: ImmutableList<String> = persistentListOf()
)
