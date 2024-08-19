package com.dev.innerview.feature.home.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.InnerViewType
import java.time.ZonedDateTime

@Immutable
data class InnerViewItemUiState(
    val id: Int = 0,
    val title: String = "",
    val type: InnerViewType = InnerViewType.YEAR,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val isDropdownMenuVisible: Boolean = false,
    val isInnerViewDeleteDialogVisible: Boolean = false
)