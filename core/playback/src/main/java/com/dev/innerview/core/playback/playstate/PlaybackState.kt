package com.dev.innerview.core.playback.playstate

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentMediaItemIndex: Int = 0,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val aspectRatio: Float = 9F / 16F,
)