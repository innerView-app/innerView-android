package com.dev.innerview.feature.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dev.innerview.core.domain.usecase.AddQuestionUseCase
import com.dev.innerview.core.domain.usecase.CancelNotificationAlarmUseCase
import com.dev.innerview.core.domain.usecase.CompleteInterviewGroupUseCase
import com.dev.innerview.core.domain.usecase.DeleteInnerProjectUseCase
import com.dev.innerview.core.domain.usecase.DeleteQuestionUseCase
import com.dev.innerview.core.domain.usecase.GetInterviewGroupContentUseCase
import com.dev.innerview.core.domain.usecase.GetRecommendQuestionsByTypeUseCase
import com.dev.innerview.core.domain.usecase.RegisterNotificationAlarmUseCase
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.navigation.Route
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
    savedStateHandle: SavedStateHandle,
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

    init {
        val (innerViewId, interviewGroupId) = savedStateHandle.toRoute<Route.Records>()
        getInterviewGroupContentUseCase(innerViewId, interviewGroupId)
            .onEach { interviewGroupContent ->
                _recordUiState.update {
                    it.copy(
                        innerViewId = innerViewId,
                        interviewGroupId = interviewGroupId,
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

    fun addQuestion() {
        viewModelScope.launch {
            runCatching {
                addQuestionUseCase(
                    _recordUiState.value.innerViewId,
                    _recordUiState.value.interviewGroupId,
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

    fun deleteQuestion(question: String) {
        viewModelScope.launch {
            deleteQuestionUseCase(
                _recordUiState.value.innerViewId,
                _recordUiState.value.interviewGroupId,
                question
            )
        }
    }

    fun deleteInnerProject(question: String) {
        viewModelScope.launch {
            deleteInnerProjectUseCase(
                _recordUiState.value.innerViewId,
                _recordUiState.value.interviewGroupId,
                question
            )
        }
    }

    fun completeInterviewGroup() {
        viewModelScope.launch {
            completeInterviewGroupUseCase(
                _recordUiState.value.innerViewId,
                _recordUiState.value.interviewGroupId
            )
            cancelNotificationAlarmUseCase(_recordUiState.value.innerViewId)
            with(recordUiState.value) {
                registerNotificationAlarmUseCase(
                    innerViewId = _recordUiState.value.innerViewId,
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