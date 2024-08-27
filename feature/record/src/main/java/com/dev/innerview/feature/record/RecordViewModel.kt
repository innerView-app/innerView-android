package com.dev.innerview.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddQuestionUseCase
import com.dev.innerview.core.domain.usecase.CompleteInterviewGroupUseCase
import com.dev.innerview.core.domain.usecase.GetInterviewUseCase
import com.dev.innerview.feature.record.model.RecordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val getInterviewUseCase: GetInterviewUseCase,
    private val addQuestionUseCase: AddQuestionUseCase,
    private val completeInterviewGroupUseCase: CompleteInterviewGroupUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _recordUiState = MutableStateFlow(RecordUiState())
    val recordUiState = _recordUiState.asStateFlow()

    fun fetchInnerView(innerViewId: String, interviewGroupId: Int) {
        getInterviewUseCase(innerViewId, interviewGroupId)
            .onEach { interviews ->
                _recordUiState.update {
                    it.copy(interviews = interviews.toPersistentList())
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
}