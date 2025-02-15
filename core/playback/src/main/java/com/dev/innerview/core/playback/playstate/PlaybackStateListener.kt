package com.dev.innerview.core.playback.playstate

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaybackStateListener @Inject constructor(
    private val scope: CoroutineScope,
    private val playbackStateManager: PlaybackStateManager
) : Player.Listener {

    private lateinit var player: Player
    private var job: Job? = null

    fun attachTo(player: Player) {
        this.player = player
        player.addListener(this)

        job?.cancel()
        job = scope.launch {
            playbackStateManager.flow
                .map { it.isPlaying }
                .collectLatest { isPlaying ->
                    if (isPlaying) {
                        while (true) {
                            updatePlayState()
                            delay(10L)
                        }
                    }
                }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        updatePlayState()
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        updatePlayState()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        updatePlayState()
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        updatePlayState()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        updatePlayState()
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        updatePlayState()
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        updatePlayState()
    }

    fun cancelJob() {
        job?.cancel()
    }

    private fun updatePlayState() {
        val playbackState = player.playbackState
        playbackStateManager.playbackState = PlaybackState(
            isPlaying = when {
                playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE -> false
                player.playWhenReady -> true
                else -> false
            },
            currentMediaItemIndex = player.currentMediaItemIndex,
            currentPosition = player.currentPosition,
            duration = player.duration,
        )
    }
}