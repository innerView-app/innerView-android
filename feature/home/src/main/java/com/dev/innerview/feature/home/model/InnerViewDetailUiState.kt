package com.dev.innerview.feature.home.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.InterviewGroup
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@Immutable
data class InnerViewDetailUiState(
    val title: String = "",
    val type: InnerViewType = InnerViewType.YEAR,
    val interviewGroups: ImmutableList<InterviewGroup> = persistentListOf(),
    val reactivateAt: LocalDate = LocalDate.now(),
    val isActivated: Boolean = true,
    val isNotificationOn: Boolean = true,
    val isRecording: Boolean = false
)
