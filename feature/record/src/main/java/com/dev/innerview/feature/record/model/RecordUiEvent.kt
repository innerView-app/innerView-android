package com.dev.innerview.feature.record.model

sealed interface RecordUiEvent {
    data object NavigateToBack : RecordUiEvent
    data class NavigateToFilming(val question: String) : RecordUiEvent
}