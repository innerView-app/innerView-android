package com.dev.innerview.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddInterviewGroupUseCase
import com.dev.innerview.core.domain.usecase.GetInnerViewContentUseCase
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.InterviewGroup
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
    private val addInterviewGroupUseCase: AddInterviewGroupUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

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
                        interviewGroups = innerViewContent.interviewGroups.toPersistentList(),
                        reactivateAt = reactivateAt,
                        isActivated = !LocalDate.now().isBefore(reactivateAt)
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun findReactivateDate(
        interviewGroups: List<InterviewGroup>,
        innerViewType: InnerViewType
    ): LocalDate {
        return if (interviewGroups.isEmpty()) {
            LocalDate.now()
        } else {
            val lastDate = interviewGroups.first().createdAt
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDate()
            when (innerViewType) {
                InnerViewType.DAY -> lastDate.plusDays(1)
                InnerViewType.WEEK -> lastDate.plusWeeks(1)
                InnerViewType.MONTH -> lastDate.plusMonths(1)
                InnerViewType.YEAR -> lastDate.plusYears(1)
            }
        }
    }

    fun addInnerViewGroup(innerViewId: String) {
        viewModelScope.launch {
            addInterviewGroupUseCase(innerViewId)
        }
    }
}