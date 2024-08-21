package com.dev.innerview.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.AddInnerViewUseCase
import com.dev.innerview.core.domain.usecase.DeleteInnerViewUseCase
import com.dev.innerview.core.domain.usecase.GetInnerViewUseCase
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.feature.home.model.HomeUiState
import com.dev.innerview.feature.home.model.InnerViewItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getInnerViewUseCase: GetInnerViewUseCase,
    private val addInnerViewUseCase: AddInnerViewUseCase,
    private val deleteInnerViewUseCase: DeleteInnerViewUseCase
) : ViewModel() {

    val maxInnerViewTitleLength = 40

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        getInnerViewUseCase()
            .map { innerViews ->
                innerViews.map { innerView ->
                    InnerViewItemUiState(
                        id = innerView.id,
                        title = innerView.title,
                        type = innerView.type,
                        createdAt = innerView.createdAt
                    )
                }
            }.onEach { innerViewItemStates ->
                _homeUiState.update {
                    it.copy(innerViews = innerViewItemStates.toPersistentList())
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
                selectInnerViewCreate()
            }
        }
    }

    fun deleteInnerView(id: Int) {
        viewModelScope.launch {
            deleteInnerViewUseCase(id)
        }
    }

    fun selectInnerViewCreate() {
        _homeUiState.update {
            it.copy(
                isInnerViewCreateDialogVisible = !it.isInnerViewCreateDialogVisible,
                dialogInnerViewTitle = "",
                dialogSelectedType = InnerViewType.YEAR
            )
        }
    }

    fun selectInnerViewDelete(id: Int) {
        _homeUiState.update {
            it.copy(
                innerViews = it.innerViews.map { itemUiState ->
                    if (itemUiState.id == id) {
                        itemUiState.copy(
                            isInnerViewDeleteDialogVisible = !itemUiState.isInnerViewDeleteDialogVisible
                        )
                    } else {
                        itemUiState
                    }
                }.toPersistentList()
            )
        }
    }

    fun selectInnerViewDropdown(id: Int) {
        _homeUiState.update {
            it.copy(
                innerViews = it.innerViews.map { itemUiState ->
                    if (itemUiState.id == id) {
                        itemUiState.copy(
                            isDropdownMenuVisible = !itemUiState.isDropdownMenuVisible
                        )
                    } else {
                        itemUiState
                    }
                }.toPersistentList()
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