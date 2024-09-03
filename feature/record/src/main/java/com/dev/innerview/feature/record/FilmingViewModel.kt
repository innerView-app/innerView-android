package com.dev.innerview.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddInnerProjectUseCase
import com.dev.innerview.core.model.RecordState
import com.dev.innerview.feature.record.model.FilmingUiEvent
import com.dev.innerview.feature.record.model.RecordingState
import com.dev.innerview.feature.record.model.PermissionState
import com.dev.innerview.feature.record.model.FilmingUiState
import com.dev.innerview.feature.record.model.RecordUiEvent
import com.dev.innerview.feature.record.model.RecordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class FilmingViewModel @Inject constructor(
    private val addInnerProjectUseCase: AddInnerProjectUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _uiEventFlow = MutableSharedFlow<FilmingUiEvent>()
    val uiEventFlow get() = _uiEventFlow.asSharedFlow()

    private val _filmingUiState = MutableStateFlow(FilmingUiState())
    val filmingUiState = _filmingUiState.asStateFlow()

    fun fetchFilmingUiState(innerViewId: String, interviewGroupId: Int, question: String) {
        _filmingUiState.update {
            it.copy(
                outputFileName = sha256(innerViewId + interviewGroupId + question) + ".mp4"
            )
        }
    }

    fun addInnerProject(innerViewId: String, interviewGroupId: Int, question: String) {
        viewModelScope.launch {
            runCatching {
                addInnerProjectUseCase(
                    innerViewId,
                    interviewGroupId,
                    question,
                    _filmingUiState.value.outputFileName
                )
            }.onSuccess {
                _uiEventFlow.emit(FilmingUiEvent.NavigateToEdit)
            }
        }
    }

    fun updatePermissionsState(cameraState: PermissionState) {
        _filmingUiState.update {
            it.copy(
                permissionState = cameraState
            )
        }
    }

    fun selectPermissionDenied() {
        _filmingUiState.update {
            it.copy(
                isPermissionDialogVisible = !it.isPermissionDialogVisible
            )
        }
    }

    fun startRecording() {
        _filmingUiState.update {
            it.copy(
                recordingState = RecordingState(
                    recordState = RecordState.RECODING
                )
            )
        }
    }

    fun resetRecording() {
        _filmingUiState.update {
            it.copy(
                recordingState = RecordingState(
                    recordState = RecordState.IDLE
                )
            )
        }
    }

    fun pauseRecording() {
        if (_filmingUiState.value.recordingState.recordState == RecordState.RECODING) {
            _filmingUiState.update {
                it.copy(
                    recordingState = it.recordingState.copy(
                        recordState = RecordState.PAUSE
                    )
                )
            }
        }
    }

    fun resumeRecording() {
        if (_filmingUiState.value.recordingState.recordState == RecordState.PAUSE) {
            _filmingUiState.update {
                it.copy(
                    recordingState = it.recordingState.copy(
                        recordState = RecordState.RECODING
                    )
                )
            }
        }
    }

    fun updateFlashState(hasFlash: Boolean) {
        _filmingUiState.update {
            it.copy(
                recordingState = it.recordingState.copy(
                    isFlashOn = hasFlash
                )
            )
        }
    }

    fun updateRecordingState(duration: Long, sizeByte: Long) {
        if (_filmingUiState.value.recordingState.recordState == RecordState.RECODING) {
            _filmingUiState.update {
                it.copy(
                    recordingState = it.recordingState.copy(
                        recordedDurationNanos = duration,
                        numBytesRecorded = sizeByte
                    )
                )
            }
        }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}