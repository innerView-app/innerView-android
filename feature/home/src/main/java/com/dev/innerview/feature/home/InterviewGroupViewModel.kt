package com.dev.innerview.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dev.innerview.core.domain.usecase.GetInterviewGroupContentUseCase
import com.dev.innerview.core.navigation.Route
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
    savedStateHandle: SavedStateHandle,
    private val getInterviewGroupContentUseCase: GetInterviewGroupContentUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _interviewGroupUiState = MutableStateFlow(InterviewGroupUiState())
    val interviewGroupUiState get() = _interviewGroupUiState.asStateFlow()

    init {
        val (innerViewId, interviewGroupId) = savedStateHandle.toRoute<Route.InterviewGroup>()
        getInterviewGroupContentUseCase(innerViewId, interviewGroupId)
            .onEach { interviewGroupContent ->
                _interviewGroupUiState.update {
                    it.copy(
                        innerViewId = innerViewId,
                        interviewGroupId = interviewGroupId,
                        title = interviewGroupContent.innerView.title,
                        createdAt = interviewGroupContent.interviewGroup.createdAt,
                        interviews = interviewGroupContent.interviews.toPersistentList()
                    )
                }
            }.launchIn(viewModelScope)
    }
}