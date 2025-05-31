package com.dev.innerview.core.model

sealed class RenderProgress {
    data class InProgress(val percentage: Int = 0) : RenderProgress()
    data object Completed : RenderProgress()
    data object Cancelled : RenderProgress()
    data object Error : RenderProgress()
}