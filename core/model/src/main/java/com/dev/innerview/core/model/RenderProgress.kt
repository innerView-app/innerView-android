package com.dev.innerview.core.model

sealed class RenderProgress {
    data object Idle : RenderProgress()
    data class InProgress(val percentage: Int) : RenderProgress()
    data object Completed : RenderProgress()
    data object Cancelled : RenderProgress()
    data object Error : RenderProgress()
}