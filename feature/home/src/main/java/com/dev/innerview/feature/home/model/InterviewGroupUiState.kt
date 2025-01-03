package com.dev.innerview.feature.home.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.Interview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.ZonedDateTime

@Immutable
data class InterviewGroupUiState(
    val title: String = "",
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val interviews: ImmutableList<Interview> = persistentListOf()
)
