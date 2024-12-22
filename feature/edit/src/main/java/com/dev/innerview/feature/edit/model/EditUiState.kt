package com.dev.innerview.feature.edit.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.core.model.RecordState
import com.dev.innerview.core.model.Subtitle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class EditUiState(
    val innerProjectId: Int = 0,
    val title: String = "",
    val recordState: RecordState = RecordState.RECODING,
    val isPlaying: Boolean = false,
    val currentMediaItemIndex: Int = 0,
    val position: Long = 0,
    val duration: Long = 0,
    val aspectRatio: Float = 9F / 16F,
    val zoom: Float = 0.1f,
    val media: ImmutableList<InterviewPiece> = persistentListOf(),
    val accumulatedDurations: ImmutableList<Long> = persistentListOf(),
    val subtitles: ImmutableList<Subtitle> = persistentListOf(),
)