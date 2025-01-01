package com.dev.innerview.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddInterviewGroupUseCase
import com.dev.innerview.core.domain.usecase.CancelNotificationAlarmUseCase
import com.dev.innerview.core.domain.usecase.ChangeInnerViewNotificationUseCase
import com.dev.innerview.core.domain.usecase.GetInnerViewContentUseCase
import com.dev.innerview.core.domain.usecase.RegisterNotificationAlarmUseCase
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.InterviewGroup
import com.dev.innerview.feature.home.model.InnerViewDetailUiEvent
import com.dev.innerview.feature.home.model.InnerViewDetailUiState
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
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class InnerViewDetailViewModel @Inject constructor(
    private val getInnerViewContentUseCase: GetInnerViewContentUseCase,
    private val addInterviewGroupUseCase: AddInterviewGroupUseCase,
    private val changeInnerViewNotificationUseCase: ChangeInnerViewNotificationUseCase,
    private val registerNotificationAlarmUseCase: RegisterNotificationAlarmUseCase,
    private val cancelNotificationAlarmUseCase: CancelNotificationAlarmUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _uiEventFlow = MutableSharedFlow<InnerViewDetailUiEvent>()
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    private val _innerViewDetailUiState = MutableStateFlow(InnerViewDetailUiState())
    val innerViewDetailUiState = _innerViewDetailUiState.asStateFlow()

    fun fetchInnerView(innerViewId: String) {
        getInnerViewContentUseCase(innerViewId)
            .onEach { innerViewContent ->
                val reactivateAt = findReactivateDate(
                    innerViewContent.interviewGroups,
                    innerViewContent.innerView.type
                )
                _innerViewDetailUiState.update {
                    it.copy(
                        title = innerViewContent.innerView.title,
                        type = innerViewContent.innerView.type,
                        interviewGroups = innerViewContent.interviewGroups.toPersistentList(),
                        reactivateAt = reactivateAt,
                        isActivated = !LocalDate.now().isBefore(reactivateAt),
                        isNotificationOn = innerViewContent.innerView.isNotificationOn,
                        isRecording = innerViewContent.interviewGroups.firstOrNull()?.isRecording
                            ?: false
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun findReactivateDate(
        interviewGroups: List<InterviewGroup>,
        innerViewType: InnerViewType
    ): LocalDate {
        val lastDate = interviewGroups.firstOrNull()
            ?.createdAt
            ?.withZoneSameInstant(ZoneId.systemDefault())
            ?.toLocalDate()
            ?: return LocalDate.now()
        return when (innerViewType) {
            InnerViewType.DAY -> lastDate.plusDays(1)
            InnerViewType.WEEK -> lastDate.plusWeeks(1)
            InnerViewType.MONTH -> lastDate.plusMonths(1)
            InnerViewType.YEAR -> lastDate.plusYears(1)
        }
    }

    fun changeNotificationState(innerViewId: String, isOn: Boolean) {
        viewModelScope.launch {
            changeInnerViewNotificationUseCase(innerViewId, isOn)
            if (isOn) {
                with(innerViewDetailUiState.value) {
                    registerNotificationAlarmUseCase(
                        innerViewId = innerViewId,
                        innerViewType = type,
                        innerViewTitle = title,
                        lastInnerViewTime = interviewGroups
                            .find { !it.isRecording }
                            ?.createdAt
                            ?: return@with
                    )
                }
            } else {
                cancelNotificationAlarmUseCase(innerViewId)
            }
            _uiEventFlow.emit(InnerViewDetailUiEvent.TurnNotificationEvent(isOn))
        }
    }

    fun addInnerViewGroup(innerViewId: String) {
        viewModelScope.launch {
            addInterviewGroupUseCase(
                innerViewId,
                _innerViewDetailUiState.value.type != InnerViewType.DAY
            )
        }
    }
}