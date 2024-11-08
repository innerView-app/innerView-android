package com.dev.innerview.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.GetInnerViewContentUseCase
import com.dev.innerview.core.domain.usecase.ReorderQuestionsUseCase
import com.dev.innerview.feature.home.model.InnerViewQuestionUiState
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
class InnerViewQuestionViewModel @Inject constructor(
    private val getInnerViewContentUseCase: GetInnerViewContentUseCase,
    private val reorderQuestionsUseCase: ReorderQuestionsUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _innerViewQuestionUiState = MutableStateFlow(InnerViewQuestionUiState())
    val innerViewQuestionUiState = _innerViewQuestionUiState.asStateFlow()

    fun fetchInnerViewQuestions(innerViewId: String) {
        getInnerViewContentUseCase(innerViewId)
            .onEach { innerViewContent ->
                _innerViewQuestionUiState.update {
                    it.copy(
                        title = innerViewContent.innerView.title,
                        interviewQuestions = innerViewContent.innerView.questions.toPersistentList()
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun updateQuestions(innerViewId: String, oldIndex: Int, newIndex: Int) {
        reorderQuestions(oldIndex, newIndex)
        viewModelScope.launch {
            runCatching {
                reorderQuestionsUseCase(
                    innerViewId,
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