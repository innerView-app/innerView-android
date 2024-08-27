package com.dev.innerview.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddQuestionUseCase
import com.dev.innerview.core.domain.usecase.CompleteInterviewGroupUseCase
import com.dev.innerview.core.domain.usecase.DeleteInnerProjectUseCase
import com.dev.innerview.core.domain.usecase.DeleteQuestionUseCase
import com.dev.innerview.core.domain.usecase.GetInterviewUseCase
import com.dev.innerview.feature.record.model.InterviewItemUiState
import com.dev.innerview.feature.record.model.RecordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val getInterviewUseCase: GetInterviewUseCase,
    private val addQuestionUseCase: AddQuestionUseCase,
    private val completeInterviewGroupUseCase: CompleteInterviewGroupUseCase,
    private val deleteQuestionUseCase: DeleteQuestionUseCase,
    private val deleteInnerProjectUseCase: DeleteInnerProjectUseCase,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _recordUiState = MutableStateFlow(RecordUiState())
    val recordUiState = _recordUiState.asStateFlow()

    fun fetchInnerView(innerViewId: String, interviewGroupId: Int) {
        getInterviewUseCase(innerViewId, interviewGroupId)
            .map { interviews ->
                interviews.map { InterviewItemUiState(interview = it) }
            }.onEach { interviewItemStates ->
                _recordUiState.update {
                    it.copy(interviews = interviewItemStates.toPersistentList())
                }
            }.launchIn(viewModelScope)
    }

    fun selectQuestionAdd() {
        _recordUiState.update {
            it.copy(
                isQuestionAddDialogVisible = !it.isQuestionAddDialogVisible,
                selectableQuestions = persistentListOf("1", "2", "3", ""),
                selectedQuestion = 0
            )
        }
    }

    fun addQuestion(innerViewId: String, interviewGroupId: Int) {
        viewModelScope.launch {
            runCatching {
                addQuestionUseCase(
                    innerViewId,
                    interviewGroupId,
                    _recordUiState.value.let {
                        it.selectableQuestions[it.selectedQuestion]
                    }
                )
            }.onFailure { throwable ->
                _errorFlow.emit(throwable)
            }
            selectQuestionAdd()
        }
    }

    fun deleteQuestion(innerViewId: String, interviewGroupId: Int, question: String) {
        viewModelScope.launch {
            deleteQuestionUseCase(innerViewId, interviewGroupId, question)
        }
    }

    fun deleteInnerProject(innerViewId: String, interviewGroupId: Int, question: String) {
        viewModelScope.launch {
            deleteInnerProjectUseCase(innerViewId, interviewGroupId, question)
        }
    }

    fun completeInterviewGroup(innerViewId: String, interviewGroupId: Int) {
        viewModelScope.launch {
            completeInterviewGroupUseCase(innerViewId, interviewGroupId)
        }
    }

    fun updateCustomQuestion(question: String) {
        _recordUiState.update {
            it.copy(
                selectableQuestions = (it.selectableQuestions.slice(0 until it.selectableQuestions.size - 1) + listOf(
                    question
                )).toPersistentList()
            )
        }
    }

    fun updateDialogSelectedType(i: Int) {
        _recordUiState.update {
            it.copy(selectedQuestion = i)
        }
    }

    fun selectQuestionDelete(question: String) {
        _recordUiState.update {
            it.copy(
                interviews = it.interviews.map { itemUiState ->
                    if (itemUiState.interview.question == question && !itemUiState.interview.isRequired) {
                        itemUiState.copy(
                            isQuestionDeleteDialogVisible = !itemUiState.isQuestionDeleteDialogVisible
                        )
                    } else {
                        itemUiState
                    }
                }.toPersistentList()
            )
        }
    }

    fun selectInnerProjectDelete(question: String) {
        _recordUiState.update {
            it.copy(
                interviews = it.interviews.map { itemUiState ->
                    if (itemUiState.interview.question == question) {
                        itemUiState.copy(
                            isInnerProjectDeleteDialogVisible = !itemUiState.isInnerProjectDeleteDialogVisible
                        )
                    } else {
                        itemUiState
                    }
                }.toPersistentList()
            )
        }
    }

    fun selectInterviewDropdown(question: String) {
        _recordUiState.update {
            it.copy(
                interviews = it.interviews.map { itemUiState ->
                    if (itemUiState.interview.question == question && (itemUiState.interview.isRecordComplete || !itemUiState.interview.isRequired)) {
                        itemUiState.copy(
                            isDropdownMenuVisible = !itemUiState.isDropdownMenuVisible
                        )
                    } else {
                        itemUiState
                    }
                }.toPersistentList()
            )
        }
    }
}