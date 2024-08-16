package com.dev.innerview.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.innerview.core.domain.usecase.GetInnerViewUseCase
import com.dev.innerview.feature.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getInnerViewUseCase: GetInnerViewUseCase
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _homeUiState =
        MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val homeUiState = _homeUiState.asStateFlow()

    init {
        getInnerViewUseCase()
            .onEach { innerViews ->
                _homeUiState.value = HomeUiState.UiState(
                    innerViews = innerViews.toPersistentList()
                )
            }.launchIn(viewModelScope)
    }
}