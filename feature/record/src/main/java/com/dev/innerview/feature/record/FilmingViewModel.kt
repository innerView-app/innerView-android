package com.dev.innerview.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddInnerProjectUseCase
import com.dev.innerview.core.domain.usecase.GetInterviewByQuestionUseCase
import com.dev.innerview.core.model.InnerProjectComponents
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.core.model.RecordState
import com.dev.innerview.feature.record.model.FilmingUiEvent
import com.dev.innerview.feature.record.model.FilmingUiState
import com.dev.innerview.feature.record.model.PermissionState
import com.dev.innerview.feature.record.model.RecordingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class FilmingViewModel @Inject constructor(
    private val addInnerProjectUseCase: AddInnerProjectUseCase,
    private val getInterviewByQuestionUseCase: GetInterviewByQuestionUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _uiEventFlow = MutableSharedFlow<FilmingUiEvent>()
    val uiEventFlow get() = _uiEventFlow.asSharedFlow()

    private val _filmingUiState = MutableStateFlow(FilmingUiState())
    val filmingUiState = _filmingUiState.asStateFlow()

    fun fetchFilmingUiState(innerViewId: String, interviewGroupId: Int, question: String) {
        getInterviewByQuestionUseCase(innerViewId, question)
            .onEach { interviews ->
                _filmingUiState.update {
                    it.copy(
                        outputFileName = sha256(innerViewId + interviewGroupId + question) + ".mp4",
                        interviews = interviews.toPersistentList()
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun addInnerProject(innerViewId: String, interviewGroupId: Int, question: String, duration: Long) {
        viewModelScope.launch {
            runCatching {
                val innerProjectId = addInnerProjectUseCase(
                    question,
                    innerViewId,
                    interviewGroupId,
                    InnerProjectComponents(
                        media = listOf(
                            InterviewPiece(
                                filePath = "interviews/${_filmingUiState.value.outputFileName}",
                                startPosition = 0L,
                                endPosition = duration,
                                duration = duration
                            )
                        )
                    )
                )
                _uiEventFlow.emit(FilmingUiEvent.NavigateToEdit(innerProjectId))
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

    fun selectBottomSheet() {
        _filmingUiState.update {
            it.copy(
                isSheetOpen = !it.isSheetOpen
            )
        }
    }

    fun startRecording() {
        _filmingUiState.update {
            it.copy(
                recordingState = RecordingState(
                    recordState = RecordState.RECORDING
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
        if (_filmingUiState.value.recordingState.recordState == RecordState.RECORDING) {
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
                        recordState = RecordState.RECORDING
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
        if (_filmingUiState.value.recordingState.recordState == RecordState.RECORDING) {
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