package com.dev.innerview.feature.home.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.InterviewGroup
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class InnerViewDetailUiState(
    val interviewGroups: ImmutableList<InterviewGroup> = persistentListOf(),
)
