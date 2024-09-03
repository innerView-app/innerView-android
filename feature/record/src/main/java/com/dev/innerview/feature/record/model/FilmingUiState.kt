package com.dev.innerview.feature.record.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.dev.innerview.core.model.Interview
import com.dev.innerview.core.model.RecordState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class FilmingUiState(
    val permissionState: PermissionState = PermissionState.Loading,
    val isPermissionDialogVisible: Boolean = false,
    val outputFileName: String = "sample.mp4",
    val recordingState: RecordingState = RecordingState(),
    val interviews: ImmutableList<Interview> = persistentListOf(),
    val isSheetOpen: Boolean = false
)

@Stable
sealed interface PermissionState {
    @Immutable
    data object Loading : PermissionState

    @Immutable
    data object Denied : PermissionState

    @Immutable
    data object Granted : PermissionState
}

@Immutable
data class RecordingState(
    val recordState: RecordState = RecordState.IDLE,
    val isFlashOn: Boolean = false,
    val recordedDurationNanos: Long = 0L,
    val numBytesRecorded: Long = 0L
)

sealed interface FilmingUiEvent {
    data object NavigateToEdit : FilmingUiEvent
}