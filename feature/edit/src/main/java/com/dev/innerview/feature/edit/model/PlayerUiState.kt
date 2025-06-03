package com.dev.innerview.feature.edit.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.Subtitle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PlayerUiState(
    val innerProjectIds: ImmutableList<Int> = persistentListOf(),
    val title: String = "",
    val isRenderDialogVisible: Boolean = false,
    val isPlaying: Boolean = false,
    val currentMediaItemIndex: Int = 0,
    val position: Long = 0,
    val duration: Long = 0,
    val aspectRatio: Float = 9F / 16F,
    val media: ImmutableList<MediaUiState> = persistentListOf(),
    val accumulatedDurations: ImmutableList<Long> = persistentListOf(),
    val subtitles: ImmutableList<Subtitle> = persistentListOf(),
)