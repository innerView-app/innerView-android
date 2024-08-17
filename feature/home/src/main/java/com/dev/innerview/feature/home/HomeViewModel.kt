package com.dev.innerview.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddInnerViewUseCase
import com.dev.innerview.core.domain.usecase.GetInnerViewUseCase
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.feature.home.model.HomeUiState
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
class HomeViewModel @Inject constructor(
    getInnerViewUseCase: GetInnerViewUseCase,
    private val addInnerViewUseCase: AddInnerViewUseCase
) : ViewModel() {

    val maxInnerViewTitleLength = 40

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        getInnerViewUseCase()
            .onEach { innerViews ->
                _homeUiState.update {
                    it.copy(innerViews = innerViews.toPersistentList())
                }
            }.launchIn(viewModelScope)
    }

    fun addInnerView() {
        viewModelScope.launch {
            if (_homeUiState.value.dialogInnerViewTitle.isNotEmpty()) {
                addInnerViewUseCase(
                    _homeUiState.value.dialogInnerViewTitle,
                    _homeUiState.value.dialogSelectedType
                )
                closeInnerViewCreateDialog()
            }
        }
    }

    fun openInnerViewCreateDialog() {
        _homeUiState.update {
            it.copy(isInnerViewCreateDialogVisible = true)
        }
    }

    fun closeInnerViewCreateDialog() {
        _homeUiState.update {
            it.copy(
                isInnerViewCreateDialogVisible = false,
                dialogInnerViewTitle = "",
                dialogSelectedType = InnerViewType.YEAR
            )
        }
    }

    fun updateDialogInnerViewTitle(title: String) {
        if (title.length <= maxInnerViewTitleLength) {
            _homeUiState.update {
                it.copy(dialogInnerViewTitle = title)
            }
        }
    }

    fun updateDialogSelectedType(type: InnerViewType) {
        _homeUiState.update {
            it.copy(dialogSelectedType = type)
        }
    }
}