package com.dev.innerview.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dev.innerview.core.domain.usecase.GetInnerViewContentUseCase
import com.dev.innerview.core.domain.usecase.ReorderQuestionsUseCase
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.feature.home.model.InnerViewQuestionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InnerViewQuestionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getInnerViewContentUseCase: GetInnerViewContentUseCase,
    private val reorderQuestionsUseCase: ReorderQuestionsUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _innerViewQuestionUiState = MutableStateFlow(InnerViewQuestionUiState())
    val innerViewQuestionUiState = _innerViewQuestionUiState.asStateFlow()

    init {
        val innerViewId = savedStateHandle.toRoute<Route.InnerViewQuestion>().id
        viewModelScope.launch {
            val innerViewContent = getInnerViewContentUseCase(innerViewId).first()
            _innerViewQuestionUiState.update {
                it.copy(
                    innerViewId = innerViewId,
                    title = innerViewContent.innerView.title,
                    interviewQuestions = innerViewContent.innerView.questions.toPersistentList()
                )
            }
        }
    }

    fun updateQuestions(oldIndex: Int, newIndex: Int) {
        reorderQuestions(oldIndex, newIndex)
        viewModelScope.launch {
            runCatching {
                reorderQuestionsUseCase(
                    _innerViewQuestionUiState.value.innerViewId,
                    oldIndex,
                    newIndex
                )
            }.onFailure {
                _errorFlow.emit(IllegalArgumentException())
            }
        }
    }

    private fun reorderQuestions(oldIndex: Int, newIndex: Int) {
        val tmp = _innerViewQuestionUiState.value.interviewQuestions.toMutableList()

        val item = tmp.removeAt(oldIndex)
        tmp.add(newIndex, item)

        _innerViewQuestionUiState.update {
            it.copy(interviewQuestions = tmp.toPersistentList())
        }
    }
}