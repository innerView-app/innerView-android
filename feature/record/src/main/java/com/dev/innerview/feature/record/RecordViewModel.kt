package com.dev.innerview.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddQuestionUseCase
import com.dev.innerview.core.domain.usecase.CancelNotificationAlarmUseCase
import com.dev.innerview.core.domain.usecase.CompleteInterviewGroupUseCase
import com.dev.innerview.core.domain.usecase.DeleteInnerProjectUseCase
import com.dev.innerview.core.domain.usecase.DeleteQuestionUseCase
import com.dev.innerview.core.domain.usecase.GetInterviewGroupContentUseCase
import com.dev.innerview.core.domain.usecase.GetRecommendQuestionsByTypeUseCase
import com.dev.innerview.core.domain.usecase.RegisterNotificationAlarmUseCase
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.feature.record.model.InterviewItemUiState
import com.dev.innerview.feature.record.model.RecordUiEvent
import com.dev.innerview.feature.record.model.RecordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
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
    private val getInterviewGroupContentUseCase: GetInterviewGroupContentUseCase,
    private val addQuestionUseCase: AddQuestionUseCase,
    private val completeInterviewGroupUseCase: CompleteInterviewGroupUseCase,
    private val deleteQuestionUseCase: DeleteQuestionUseCase,
    private val deleteInnerProjectUseCase: DeleteInnerProjectUseCase,
    private val getRecommendQuestionsByTypeUseCase: GetRecommendQuestionsByTypeUseCase,
    private val registerNotificationAlarmUseCase: RegisterNotificationAlarmUseCase,
    private val cancelNotificationAlarmUseCase: CancelNotificationAlarmUseCase
) : ViewModel() {

    private val recommendQuestionCount = 3
    private val recommendPrevQuestionCount = 1

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _uiEventFlow = MutableSharedFlow<RecordUiEvent>()
    val uiEventFlow get() = _uiEventFlow.asSharedFlow()

    private val _recordUiState = MutableStateFlow(RecordUiState())
    val recordUiState = _recordUiState.asStateFlow()

    fun fetchInterviewGroup(innerViewId: String, interviewGroupId: Int) {
        getInterviewGroupContentUseCase(innerViewId, interviewGroupId)
            .onEach { interviewGroupContent ->
                _recordUiState.update {
                    it.copy(
                        title = interviewGroupContent.innerView.title,
                        type = interviewGroupContent.innerView.type,
                        createdAt = interviewGroupContent.interviewGroup.createdAt,
                        pervQuestions = interviewGroupContent.innerView.questions.toPersistentList(),
                        interviews = interviewGroupContent.interviews.map { interview ->
                            InterviewItemUiState(interview = interview)
                        }.toPersistentList()
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun selectQuestionAdd() {
        _recordUiState.update {
            it.copy(
                isQuestionAddDialogVisible = !it.isQuestionAddDialogVisible,
                selectableQuestions = getRecommendQuestions(),
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
                selectQuestionAdd()
            }.onSuccess {
                val question =
                    _recordUiState.value.selectableQuestions[_recordUiState.value.selectedQuestion]
                _uiEventFlow.emit(RecordUiEvent.NavigateToFilming(question))
                selectQuestionAdd()
            }
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
            cancelNotificationAlarmUseCase(innerViewId)
            with(recordUiState.value) {
                registerNotificationAlarmUseCase(
                    innerViewId = innerViewId,
                    innerViewType = type,
                    innerViewTitle = title,
                    lastInnerViewTime = createdAt
                )
            }
            _uiEventFlow.emit(RecordUiEvent.NavigateToBack)
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

    fun updateRecommendQuestions() {
        _recordUiState.update {
            it.copy(
                selectableQuestions = getRecommendQuestions()
            )
        }
    }

    private fun getRecommendQuestions(): ImmutableList<String> {

        val recommendQuestions =
            getRecommendQuestionsByTypeUseCase(_recordUiState.value.type).shuffled()
        val prevQuestions = _recordUiState.value.pervQuestions
        val currentQuestions =
            _recordUiState.value.interviews.map { it.interview.question }.toPersistentList()

        return when (_recordUiState.value.type) {
            InnerViewType.DAY -> {
                val selectablePrevQuestions =
                    (prevQuestions - currentQuestions).shuffled().take(recommendPrevQuestionCount)

                val selectableQuestions =
                    (recommendQuestions - prevQuestions - currentQuestions)
                        .take(recommendQuestionCount - selectablePrevQuestions.size)

                (selectablePrevQuestions + selectableQuestions)
                    .plus("")
                    .toPersistentList()
            }

            else -> {
                val selectableQuestions = recommendQuestions - prevQuestions - currentQuestions

                selectableQuestions.take(recommendQuestionCount)
                    .plus("")
                    .toPersistentList()
            }
        }
    }
}