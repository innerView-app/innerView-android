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
import com.dev.innerview.feature.edit.model.SplitOption
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
            val media = innerProject.innerProjectComponents.media.map { mediaItem ->
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
        val mediaItem = newMedia.last().toMediaItem()
        player.addMediaItem(mediaItem)
        player.prepare()
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

    private fun seekToPosition(positionMs: Long, accumulatedDurations: List<Long>) {
        player.pause()
        val itemIndex = calculateMediaItemIndex(positionMs)

        if (itemIndex > 0 && itemIndex >= accumulatedDurations.size - 1) {
            player.seekTo(
                itemIndex - 1,
                accumulatedDurations[itemIndex] - accumulatedDurations[itemIndex - 1]
            )
        } else {
            player.seekTo(itemIndex, positionMs - accumulatedDurations[itemIndex])
        }
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

    fun cancelMediaItem() {
        _editUiState.update {
            it.copy(
                media = it.media.map { it.copy(selected = false) }.toPersistentList()
            )
        }
    }

    fun splitMediaItem(splitOption: SplitOption = SplitOption.NONE) {
        player.pause()
        with(_editUiState.value) {
            if (media[currentMediaItemIndex].selected) {
                val cutPosition = position - accumulatedDurations[currentMediaItemIndex]
                if (cutPosition == 0L || cutPosition + 1 >= media[currentMediaItemIndex].medium.endPosition) return

                media[currentMediaItemIndex].medium.startPosition + cutPosition


                val splitMediaUiStateList = mutableListOf<MediaUiState>()
                when (splitOption) {
                    SplitOption.NONE -> {
                        splitMediaUiStateList.addAll(
                            listOf(
                                MediaUiState(
                                    medium = media[currentMediaItemIndex].medium.copy(
                                        startPosition = media[currentMediaItemIndex].medium.startPosition,
                                        endPosition = media[currentMediaItemIndex].medium.startPosition + cutPosition - 1
                                    ),
                                    selected = false
                                ),
                                MediaUiState(
                                    medium = media[currentMediaItemIndex].medium.copy(
                                        startPosition = media[currentMediaItemIndex].medium.startPosition + cutPosition,
                                        endPosition = media[currentMediaItemIndex].medium.endPosition
                                    ),
                                    selected = true
                                )
                            )
                        )
                    }

                    SplitOption.LEFT -> {
                        splitMediaUiStateList.add(
                            MediaUiState(
                                medium = media[currentMediaItemIndex].medium.copy(
                                    startPosition = media[currentMediaItemIndex].medium.startPosition + cutPosition,
                                    endPosition = media[currentMediaItemIndex].medium.endPosition
                                ),
                                selected = true
                            )
                        )
                    }

                    SplitOption.RIGHT -> {
                        splitMediaUiStateList.add(
                            MediaUiState(
                                medium = media[currentMediaItemIndex].medium.copy(
                                    startPosition = media[currentMediaItemIndex].medium.startPosition,
                                    endPosition = media[currentMediaItemIndex].medium.startPosition + cutPosition - 1
                                ),
                                selected = true
                            )
                        )
                    }
                }

                val newMedia = media.toPersistentList().removeAt(currentMediaItemIndex).addAll(
                    currentMediaItemIndex, splitMediaUiStateList
                )
                val newAccumulatedDurations =
                    calculateAccumulatedDurations(newMedia).toPersistentList()

                _editUiState.update {
                    it.copy(
                        media = newMedia,
                        duration = newAccumulatedDurations.last(),
                        accumulatedDurations = newAccumulatedDurations,
                    )
                }

                player.replaceMediaItems(
                    currentMediaItemIndex,
                    currentMediaItemIndex + 1,
                    splitMediaUiStateList.map { it.toMediaItem() })
                player.prepare()

                when (splitOption) {
                    SplitOption.LEFT -> {
                        player.seekTo(currentMediaItemIndex, 0L)
                    }

                    else -> {
                        seekToPosition(position, newAccumulatedDurations)
                    }
                }
            }
        }
    }

    fun deleteMediaItem() {
        player.pause()
        with(_editUiState.value) {

            val selectedItemIndex = media.indexOfFirst { it.selected }
            val newMedia = media.toPersistentList().removeAt(selectedItemIndex)
            val newAccumulatedDurations = calculateAccumulatedDurations(newMedia).toPersistentList()

            _editUiState.update {
                it.copy(
                    media = newMedia,
                    duration = newAccumulatedDurations.last(),
                    accumulatedDurations = newAccumulatedDurations
                )
            }

            player.removeMediaItem(selectedItemIndex)
            player.prepare()
            seekToPosition(position, newAccumulatedDurations)
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

    private fun MediaUiState.toMediaItem(): MediaItem {
        val videoFile = File(context.filesDir, this.medium.filePath)
        val mediaUri = videoFile.toUri()

        val mediaItem = MediaItem.Builder()
            .setUri(mediaUri)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(this.medium.startPosition)
                    .setEndPositionMs(this.medium.endPosition)
                    .build()
            )
            .build()
        return mediaItem
    }

    override fun onCleared() {
        super.onCleared()
        playbackStateListener.cancelJob()
        player.release()
    }
}