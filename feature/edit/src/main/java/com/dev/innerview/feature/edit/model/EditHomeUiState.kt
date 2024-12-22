package com.dev.innerview.feature.edit.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.InnerProject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class EditHomeUiState(
    val innerProjects: ImmutableList<InnerProject> = persistentListOf(),
    val isInnerProjectCreateDialogVisible: Boolean = false,
    val dialogInnerProjectTitle: String = "",
)
