package com.dev.innerview.feature.edit

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.navigation.toRoute
import com.dev.innerview.core.domain.usecase.GetInnerProjectByIdUseCase
import com.dev.innerview.core.domain.usecase.GetInnerViewContentUseCase
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.core.playback.playstate.PlaybackStateListener
import com.dev.innerview.core.playback.playstate.PlaybackStateManager
import com.dev.innerview.feature.edit.model.MediaUiState
import com.dev.innerview.feature.edit.model.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val getInnerViewContentUseCase: GetInnerViewContentUseCase,
    private val getInnerProjectByIdUseCase: GetInnerProjectByIdUseCase,
    private val playbackStateManager: PlaybackStateManager,
    private val playbackStateListener: PlaybackStateListener,
    val player: Player,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState = _playerUiState.asStateFlow()

    init {
        playbackStateListener.attachTo(player)

        val innerProjectIds = savedStateHandle.toRoute<Route.Player>().innerProjectIds

        viewModelScope.launch {

            fetchInnerProjects(innerProjectIds)

            playbackStateManager.flow.collect { playbackState ->
                _playerUiState.update {
                    it.copy(
                        isPlaying = playbackState.isPlaying,
                        currentMediaItemIndex = playbackState.currentMediaItemIndex,
                        position = calculateCurrentPosition(
                            playbackState.currentMediaItemIndex,
                            playbackState.currentPosition
                        ),
                        aspectRatio = playbackState.aspectRatio
                    )
                }
            }
        }
    }

    private suspend fun fetchInnerProjects(innerProjectIds: List<Int>) {

        val media = mutableListOf<MediaUiState>()
        val firstInnerProject = getInnerProjectByIdUseCase(innerProjectIds.first()).first()
        val innerViewContent =
            firstInnerProject.innerViewId?.let { getInnerViewContentUseCase(it).first() }

        innerProjectIds.forEach { id ->
            val innerProject = getInnerProjectByIdUseCase(id).first()

            innerProject.innerViewId

            media.addAll(
                innerProject.innerProjectComponents.media.map { mediaItem ->
                    MediaUiState(medium = mediaItem)
                }
            )
        }

        val duration = media.sumOf {
            it.medium.endPosition - it.medium.startPosition
        }

        val accumulatedDurations =
            calculateAccumulatedDurations(media)

        setMediaItems(media.map { it.medium })

        _playerUiState.update {
            it.copy(
                innerProjectIds = innerProjectIds.toPersistentList(),
                title = if (innerProjectIds.size == 1) {
                    firstInnerProject.title
                } else {
                    innerViewContent?.innerView?.title ?: firstInnerProject.title
                },
                duration = duration,
                media = media.toPersistentList(),
                accumulatedDurations = accumulatedDurations.toPersistentList(),
            )
        }
    }

    fun seekToPosition(positionMs: Long) {
        player.pause()
        val itemIndex = calculateMediaItemIndex(positionMs)

        if (itemIndex > 0 && itemIndex >= _playerUiState.value.accumulatedDurations.size - 1) {
            player.seekTo(
                itemIndex - 1,
                _playerUiState.value.accumulatedDurations[itemIndex] - _playerUiState.value.accumulatedDurations[itemIndex - 1]
            )
        } else {
            player.seekTo(
                itemIndex,
                positionMs - _playerUiState.value.accumulatedDurations[itemIndex]
            )
        }
    }

    private fun setMediaItems(videos: List<InterviewPiece>) {
        val mediaItems = videos.map { video ->
            val videoFile = File(context.filesDir, video.filePath)
            val mediaUri = videoFile.toUri()

            val mediaItem = MediaItem.Builder()
                .setUri(mediaUri)
                .setClippingConfiguration(
                    MediaItem.ClippingConfiguration.Builder()
                        .setStartPositionMs(video.startPosition)
                        .setEndPositionMs(video.endPosition)
                        .build()
                )
                .build()
            mediaItem
        }
        player.clearMediaItems()
        player.addMediaItems(mediaItems)
        player.prepare()
        player.play()
    }

    private fun calculateCurrentPosition(
        currentMediaItemIndex: Int,
        currentMediaPosition: Long
    ): Long {
        return if (_playerUiState.value.accumulatedDurations.isEmpty()) {
            0L
        } else {
            _playerUiState.value.accumulatedDurations[currentMediaItemIndex] + currentMediaPosition
        }
    }

    private fun calculateAccumulatedDurations(media: List<MediaUiState>): List<Long> {
        return media.map { it.medium.endPosition - it.medium.startPosition }
            .runningFold(0L) { sum, item -> sum + item }
    }

    private fun calculateMediaItemIndex(positionMs: Long): Int {
        var l = 0
        var r = _playerUiState.value.accumulatedDurations.size - 1

        while (l <= r) {
            val mid = (l + r) / 2
            if (_playerUiState.value.accumulatedDurations[mid] <= positionMs) {
                l = mid + 1
            } else {
                r = mid - 1
            }
        }
        return r
    }

    override fun onCleared() {
        super.onCleared()
        playbackStateListener.cancelJob()
        player.release()
    }
}