package com.dev.innerview.feature.home.model

sealed interface InnerViewDetailUiEvent {
    data class TurnNotificationEvent(val isOn: Boolean) : InnerViewDetailUiEvent
}