package com.dev.innerview.feature.home.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HomeUiState(
    val innerViews: ImmutableList<InnerView> = persistentListOf(),
    val isInnerViewCreateDialogVisible: Boolean = false,
    val dialogInnerViewTitle: String = "",
    val dialogSelectedType: InnerViewType = InnerViewType.YEAR
)
