package com.dev.innerview.feature.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddInnerProjectUseCase
import com.dev.innerview.core.domain.usecase.DeleteInnerProjectUseCase
import com.dev.innerview.core.domain.usecase.GetInnerProjectUseCase
import com.dev.innerview.feature.edit.model.EditHomeUiState
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
class EditHomeViewModel @Inject constructor(
    getInnerProjectUseCase: GetInnerProjectUseCase,
    private val addInnerProjectUseCase: AddInnerProjectUseCase,
    private val deleteInnerProjectUseCase: DeleteInnerProjectUseCase
) : ViewModel() {

    val maxInnerProjectTitleLength = 40

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _editHomeUiState = MutableStateFlow(EditHomeUiState())
    val editHomeUiState = _editHomeUiState.asStateFlow()

    init {
        getInnerProjectUseCase()
            .onEach { innerProjects ->
                _editHomeUiState.update {
                    it.copy(innerProjects = innerProjects.toPersistentList())
                }
            }.launchIn(viewModelScope)
    }

    fun addInnerProject() {
        viewModelScope.launch {
            if (_editHomeUiState.value.dialogInnerProjectTitle.isNotEmpty()) {
                addInnerProjectUseCase(
                    title = _editHomeUiState.value.dialogInnerProjectTitle
                )
                selectInnerProjectCreate()
            }
        }
    }

    fun selectInnerProjectCreate() {
        _editHomeUiState.update {
            it.copy(
                isInnerProjectCreateDialogVisible = !it.isInnerProjectCreateDialogVisible,
                dialogInnerProjectTitle = "",
            )
        }
    }

    fun updateDialogInnerProjectTitle(title: String) {
        if (title.length <= maxInnerProjectTitleLength) {
            _editHomeUiState.update {
                it.copy(dialogInnerProjectTitle = title)
            }
        }
    }

    fun deleteInnerProject(innerProjectId: Int) {
        viewModelScope.launch {
            deleteInnerProjectUseCase(innerProjectId)
        }
    }
}