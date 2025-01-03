package com.dev.innerview.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.GetInterviewGroupContentUseCase
import com.dev.innerview.core.model.InterviewGroupContent
import com.dev.innerview.feature.home.model.InterviewGroupUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class InterviewGroupViewModel @Inject constructor(
    private val getInterviewGroupContentUseCase: GetInterviewGroupContentUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _interviewGroupUiState = MutableStateFlow(InterviewGroupUiState())
    val interviewGroupUiState get() = _interviewGroupUiState.asStateFlow()

    fun fetchInterviewGroup(innerViewId: String, interviewGroupId: Int) {
        getInterviewGroupContentUseCase(innerViewId, interviewGroupId)
            .onEach { interviewGroupContent ->
                _interviewGroupUiState.update {
                    it.copy(
                        title = interviewGroupContent.innerView.title,
                        createdAt = interviewGroupContent.interviewGroup.createdAt,
                        interviews = interviewGroupContent.interviews.toPersistentList()
                    )
                }
            }.launchIn(viewModelScope)
    }
}