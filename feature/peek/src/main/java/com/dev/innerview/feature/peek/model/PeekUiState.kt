package com.dev.innerview.feature.peek.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PeekUiState(
    val media: ImmutableList<MediaUiState> = persistentListOf(),
    val page: Int = 0,
)

@Immutable
data class MediaUiState(
    val title: String = "",
    val mediaUri: String = "",
    val userId: String = "",
    val userProfileUri: String = "",
)