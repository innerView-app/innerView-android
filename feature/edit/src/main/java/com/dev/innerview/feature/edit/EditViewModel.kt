package com.dev.innerview.feature.edit

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.dev.innerview.core.domain.usecase.GetInnerProjectByIdUseCase
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.feature.edit.model.EditUiState
import com.dev.innerview.feature.edit.model.MediaUiState
import com.dev.innerview.feature.edit.playstate.PlaybackStateListener
import com.dev.innerview.feature.edit.playstate.PlaybackStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getInnerProjectByIdUseCase: GetInnerProjectByIdUseCase,
    private val playbackStateManager: PlaybackStateManager,
    private val playbackStateListener: PlaybackStateListener,
    val player: Player,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _editUiState = MutableStateFlow(EditUiState())
    val editUiState = _editUiState.asStateFlow()

    init {
        playbackStateListener.attachTo(player)
        player.prepare()

        viewModelScope.launch {
            playbackStateManager.flow.collect { playbackState ->
                _editUiState.update {
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

    fun fetchInnerProject(id: Int) {
        getInnerProjectByIdUseCase(id).onEach { innerProject ->
            val duration = innerProject.innerProjectComponents.media.sumOf {
                it.endPosition - it.startPosition
            }
            val media = innerProject.innerProjectComponents.media.map{ mediaItem ->
                MediaUiState(medium = mediaItem)
            }
            val accumulatedDurations =
                calculateAccumulatedDurations(media)
            _editUiState.update {
                it.copy(
                    innerProjectId = id,
                    title = innerProject.title,
                    recordState = innerProject.recordState,
                    duration = duration,
                    media = media.toPersistentList(),
                    accumulatedDurations = accumulatedDurations.toPersistentList(),
                    subtitles = innerProject.innerProjectComponents.subtitles.toPersistentList(),
                )
            }
            setMediaItems(innerProject.innerProjectComponents.media)
        }.launchIn(viewModelScope)
    }

    fun addMediaItem() {
        val newMedia = _editUiState.value.media.toPersistentList().add(
            _editUiState.value.media.last()
        )
        val duration = newMedia.sumOf {
            it.medium.endPosition - it.medium.startPosition
        }
        val mediaItem = newMedia.last().let { mediaUiState ->
            val videoFile = File(context.filesDir, mediaUiState.medium.filePath)
            val mediaUri = videoFile.toUri()

            val mediaItem = MediaItem.Builder()
                .setUri(mediaUri)
                .setClippingConfiguration(
                    MediaItem.ClippingConfiguration.Builder()
                        .setStartPositionMs(mediaUiState.medium.startPosition)
                        .setEndPositionMs(mediaUiState.medium.endPosition)
                        .build()
                )
                .build()
            mediaItem
        }
        player.addMediaItem(mediaItem)
        val accumulatedDurations = calculateAccumulatedDurations(newMedia)
        _editUiState.update {
            it.copy(
                media = newMedia,
                accumulatedDurations = accumulatedDurations.toPersistentList(),
                duration = duration
            )
        }
    }

    fun updateZoom(zoom: Float) {
        _editUiState.update {
            it.copy(
                zoom = (it.zoom * zoom).coerceIn(0.05f, 1f)
            )
        }
    }

    fun seekByMediaItem(direction: Int) {
        player.pause()
        when (direction) {
            1 -> {
                if (_editUiState.value.currentMediaItemIndex == _editUiState.value.media.size - 1) {
                    player.seekTo(
                        _editUiState.value.media.size - 1,
                        _editUiState.value.media.last().medium.duration
                    )
                } else {
                    player.seekToNextMediaItem()
                }
            }

            -1 -> {
                val accumulatedDuration =
                    _editUiState.value.accumulatedDurations[_editUiState.value.currentMediaItemIndex]
                if (_editUiState.value.position - accumulatedDuration == 0L) {
                    player.seekToPreviousMediaItem()
                } else {
                    player.seekTo(_editUiState.value.currentMediaItemIndex, 0)
                }
            }
        }
    }

    fun seekByPosition(value: Long) {
        player.pause()
        val positionMs = _editUiState.value.position + value
        if (positionMs < 0) {
            player.seekTo(0, 0L)
        } else {
            val itemIndex = calculateMediaItemIndex(positionMs)
            player.seekTo(
                itemIndex,
                positionMs - _editUiState.value.accumulatedDurations[itemIndex]
            )
        }
    }

    fun seekToScrollPosition(scrollPosition: Int) {
        player.pause()
        val positionMs = (scrollPosition / _editUiState.value.zoom).toLong()
        val itemIndex = calculateMediaItemIndex(positionMs)
        player.seekTo(itemIndex, positionMs - _editUiState.value.accumulatedDurations[itemIndex])
    }

    fun selectMediaItem(index: Int) {
        _editUiState.update {
            it.copy(
                media = it.media.mapIndexed { i, uiState ->
                    when {
                        i == index -> {
                            if (!uiState.selected && _editUiState.value.currentMediaItemIndex != index) {
                                player.seekTo(index, 0L)
                            }
                            uiState.copy(selected = !uiState.selected)
                        }
                        uiState.selected -> uiState.copy(selected = false)
                        else -> uiState
                    }
                }.toPersistentList()
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
    }

    private fun calculateCurrentPosition(
        currentMediaItemIndex: Int,
        currentMediaPosition: Long
    ): Long {
        return if (_editUiState.value.accumulatedDurations.isEmpty()) {
            0L
        } else {
            _editUiState.value.accumulatedDurations[currentMediaItemIndex] + currentMediaPosition
        }
    }

    private fun calculateMediaItemIndex(positionMs: Long): Int {
        var l = 0
        var r = _editUiState.value.accumulatedDurations.size - 1

        while (l <= r) {
            val mid = (l + r) / 2
            if (_editUiState.value.accumulatedDurations[mid] <= positionMs) {
                l = mid + 1
            } else {
                r = mid - 1
            }
        }
        return r
    }

    private fun calculateAccumulatedDurations(media: List<MediaUiState>): List<Long> {
        return media.map { it.medium.endPosition - it.medium.startPosition }
            .runningFold(0L) { sum, item -> sum + item }
    }

    override fun onCleared() {
        super.onCleared()
        playbackStateListener.cancelJob()
        player.release()
    }
}