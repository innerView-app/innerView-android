package com.dev.innerview.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddInnerProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmingViewModel @Inject constructor(
    private val addInnerProjectUseCase: AddInnerProjectUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    fun addInnerProject(innerViewId: String, interviewGroupId: Int, question: String) {
        viewModelScope.launch {
            addInnerProjectUseCase(innerViewId, interviewGroupId, question)
        }
    }
}