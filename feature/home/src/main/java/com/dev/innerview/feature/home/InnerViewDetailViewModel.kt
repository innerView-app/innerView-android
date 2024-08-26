package com.dev.innerview.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddInterviewGroupUseCase
import com.dev.innerview.core.domain.usecase.GetInterviewGroupUseCase
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
import javax.inject.Inject

@HiltViewModel
class InnerViewDetailViewModel @Inject constructor(
    private val getInterviewGroupUseCase: GetInterviewGroupUseCase,
    private val addInterviewGroupUseCase: AddInterviewGroupUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _innerViewDetailUiState = MutableStateFlow(InnerViewDetailUiState())
    val innerViewDetailUiState = _innerViewDetailUiState.asStateFlow()

    fun fetchInnerView(innerViewId: String) {
        getInterviewGroupUseCase(innerViewId)
            .onEach { interviewGroups ->
                _innerViewDetailUiState.update {
                    it.copy(interviewGroups = interviewGroups.toPersistentList())
                }
            }.launchIn(viewModelScope)
    }

    fun addInnerViewGroup(innerViewId: String) {
        viewModelScope.launch {
            addInterviewGroupUseCase(innerViewId)
        }
    }
}