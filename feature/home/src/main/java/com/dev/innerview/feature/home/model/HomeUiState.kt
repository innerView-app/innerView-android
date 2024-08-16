package com.dev.innerview.feature.home.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.dev.innerview.core.model.InnerView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
sealed interface HomeUiState {

    @Immutable
    data object Loading : HomeUiState

    @Immutable
    data class UiState(
        val innerViews: ImmutableList<InnerView> = persistentListOf(),
    ) : HomeUiState
}
