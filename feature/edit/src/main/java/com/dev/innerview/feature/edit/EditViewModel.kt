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
import com.dev.innerview.core.domain.usecase.GetInnerViewUseCase
import com.dev.innerview.core.domain.usecase.GetInterviewGroupContentUseCase
import com.dev.innerview.core.domain.usecase.UpdateInnerProjectUseCase
import com.dev.innerview.core.model.InnerProjectComponents
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.core.navigation.Route
import com.dev.innerview.core.playback.playstate.PlaybackStateListener
import com.dev.innerview.core.playback.playstate.PlaybackStateManager
import com.dev.innerview.feature.edit.model.EditUiState
import com.dev.innerview.feature.edit.model.InnerViewSelectUiState
import com.dev.innerview.feature.edit.model.InterviewGroupSelectUiState
import com.dev.innerview.feature.edit.model.InterviewSelectUiState
import com.dev.innerview.feature.edit.model.MediaAddUiState
import com.dev.innerview.feature.edit.model.MediaItemSide
import com.dev.innerview.feature.edit.model.MediaUiState
import com.dev.innerview.feature.edit.model.PositionUpdateOption
import com.dev.innerview.feature.edit.model.SplitOption
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.persistentListOf
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
class EditViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val getInnerProjectByIdUseCase: GetInnerProjectByIdUseCase,
    private val updateInnerProjectUseCase: UpdateInnerProjectUseCase,
    private val getInnerViewUseCase: GetInnerViewUseCase,
    private val getInnerViewContentUseCase: GetInnerViewContentUseCase,
    private val getInterviewGroupContentUseCase: GetInterviewGroupContentUseCase,
    private val playbackStateManager: PlaybackStateManager,
    private val playbackStateListener: PlaybackStateListener,
    val player: Player,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _editUiState = MutableStateFlow(EditUiState())
    val editUiState = _editUiState.asStateFlow()

    private val _mediaAddUiState = MutableStateFlow(MediaAddUiState())
    val mediaAddUiState = _mediaAddUiState.asStateFlow()

    init {
        playbackStateListener.attachTo(player)

        val innerProjectId = savedStateHandle.toRoute<Route.Edit>().innerProjectId

        viewModelScope.launch {
            fetchInnerProject(innerProjectId)
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

    private suspend fun fetchInnerProject(id: Int) {
        val innerProject = getInnerProjectByIdUseCase(id).first()
        val duration = innerProject.innerProjectComponents.media.sumOf {
            it.endPosition - it.startPosition
        }
        val media = innerProject.innerProjectComponents.media.map { mediaItem ->
            MediaUiState(medium = mediaItem)
        }
        val accumulatedDurations =
            calculateAccumulatedDurations(media)

        setMediaItems(innerProject.innerProjectComponents.media)

        _editUiState.update {
            it.copy(
                innerProjectId = id,
                isRecording = innerProject.isRecording,
                title = innerProject.title,
                duration = duration,
                media = media.toPersistentList(),
                accumulatedDurations = accumulatedDurations.toPersistentList(),
                subtitles = innerProject.innerProjectComponents.subtitles.toPersistentList(),
            )
        }
    }

    private fun saveInnerProject() {
        viewModelScope.launch {
            with(_editUiState.value) {
                val innerProjectComponents = InnerProjectComponents(
                    media = media.map { it.medium },
                    subtitles = subtitles
                )
                runCatching {
                    updateInnerProjectUseCase(innerProjectId, innerProjectComponents)
                }.onFailure {
                    fetchInnerProject(innerProjectId)
                }
            }
        }
    }

//    fun addMediaItem() {
//        val newMedia = _editUiState.value.media.toPersistentList().add(
//            _editUiState.value.media.last()
//        )
//        val duration = newMedia.sumOf {
//            it.medium.endPosition - it.medium.startPosition
//        }
//        val mediaItem = newMedia.last().toMediaItem()
//        player.addMediaItem(mediaItem)
//        player.prepare()
//        val accumulatedDurations = calculateAccumulatedDurations(newMedia)
//        _editUiState.update {
//            it.copy(
//                media = newMedia,
//                accumulatedDurations = accumulatedDurations.toPersistentList(),
//                duration = duration
//            )
//        }
//        saveInnerProject()
//    }

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

    private fun seekToPosition(positionMs: Long) {
        player.pause()
        val itemIndex = calculateMediaItemIndex(positionMs)

        if (itemIndex > 0 && itemIndex >= _editUiState.value.accumulatedDurations.size - 1) {
            player.seekTo(
                itemIndex - 1,
                _editUiState.value.accumulatedDurations[itemIndex] - _editUiState.value.accumulatedDurations[itemIndex - 1]
            )
        } else {
            player.seekTo(
                itemIndex,
                positionMs - _editUiState.value.accumulatedDurations[itemIndex]
            )
        }
    }

    fun selectMediaItem(index: Int) {
        player.pause()
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

                _editUiState.update {
                    it.copy(
                        media = newMedia,
                    )
                }

                fetchPlayer()

                when (splitOption) {
                    SplitOption.LEFT -> {
                        player.seekTo(currentMediaItemIndex, 0L)
                    }

                    else -> {
                        seekToPosition(position)
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

            _editUiState.update {
                it.copy(
                    media = newMedia,
                )
            }

            fetchPlayer()
            seekToPosition(position)
        }
    }

    fun updateMediaItemLengthen(side: MediaItemSide, index: Int, value: Int) {
        player.pause()
        _editUiState.update {
            it.copy(
                media = it.media.mapIndexed { i, uiState ->
                    when {
                        i == index -> {
                            if (uiState.selected) {
                                when (side) {
                                    MediaItemSide.START -> {
                                        val startPosition =
                                            (uiState.medium.startPosition + value * (1 / _editUiState.value.zoom)).toLong()
                                                .coerceIn(
                                                    0,
                                                    uiState.medium.endPosition - 10
                                                )
                                        uiState.copy(
                                            medium = uiState.medium.copy(
                                                startPosition = startPosition
                                            )
                                        )
                                    }

                                    MediaItemSide.END -> {
                                        val endPosition =
                                            (uiState.medium.endPosition + value * (1 / _editUiState.value.zoom)).toLong()
                                                .coerceIn(
                                                    uiState.medium.startPosition + 10,
                                                    uiState.medium.duration
                                                )
                                        uiState.copy(
                                            medium = uiState.medium.copy(
                                                endPosition = endPosition
                                            )
                                        )
                                    }
                                }
                            } else {
                                uiState
                            }
                        }

                        else -> uiState
                    }
                }.toPersistentList()
            )
        }
        fetchPlayer()
    }

    fun updateMediaItemPosition(positionUpdateOption: PositionUpdateOption) {
        player.pause()
        with(_editUiState.value) {
            val selectedIndex = media.indexOfFirst { it.selected }
            if (selectedIndex == -1) return

            var newMedia = media.toPersistentList()
            when (positionUpdateOption) {
                PositionUpdateOption.START -> {
                    val temp = newMedia[selectedIndex]
                    newMedia = newMedia.removeAt(selectedIndex).add(0, temp)
                }

                PositionUpdateOption.LEFT -> {
                    if (selectedIndex != 0) {
                        val temp = newMedia[selectedIndex]
                        newMedia = newMedia.set(selectedIndex, newMedia[selectedIndex - 1])
                            .set(selectedIndex - 1, temp)
                    } else {
                        return
                    }
                }

                PositionUpdateOption.RIGHT -> {
                    if (selectedIndex != media.size - 1) {
                        val temp = newMedia[selectedIndex]
                        newMedia = newMedia.set(selectedIndex, newMedia[selectedIndex + 1])
                            .set(selectedIndex + 1, temp)
                    } else {
                        return
                    }
                }

                PositionUpdateOption.END -> {
                    val temp = newMedia[selectedIndex]
                    newMedia = newMedia.removeAt(selectedIndex).add(temp)
                }
            }

            _editUiState.update {
                it.copy(
                    media = newMedia,
                )
            }

            fetchPlayer()
            when (positionUpdateOption) {
                PositionUpdateOption.START -> {
                    player.seekTo(0, 0)
                }

                PositionUpdateOption.LEFT -> {
                    player.seekTo(selectedIndex - 1, 0)
                }

                PositionUpdateOption.RIGHT -> {
                    player.seekTo(selectedIndex + 1, 0)
                }

                PositionUpdateOption.END -> {
                    player.seekTo(media.size - 1, 0)
                }
            }
        }
    }

    private fun fetchPlayer() {
        val currentPosition = _editUiState.value.position
        val newAccumulatedDurations =
            calculateAccumulatedDurations(_editUiState.value.media).toPersistentList()

        _editUiState.update {
            it.copy(
                duration = newAccumulatedDurations.last(),
                accumulatedDurations = newAccumulatedDurations,
            )
        }

        player.clearMediaItems()
        player.addMediaItems(_editUiState.value.media.map { it.toMediaItem() })
        seekToPosition(currentPosition)
        player.prepare()

        saveInnerProject()
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

    fun selectMediaAddBottomSheet() {
        viewModelScope.launch {
            _mediaAddUiState.update {
                it.copy(
                    isOpen = !it.isOpen,
                    innerViews = if (!it.isOpen && it.innerViews.isEmpty()) {
                        getInnerViewUseCase().first().map { innerView ->
                            InnerViewSelectUiState.create(innerView)
                        }.toPersistentList()
                    } else {
                        it.innerViews
                    }
                )
            }
        }
    }

    fun selectInnerViewItem(id: String) {
        viewModelScope.launch {
            _mediaAddUiState.update {
                it.copy(
                    innerViews = it.innerViews.map { innerView ->
                        if (innerView.innerViewId == id) {
                            innerView.copy(
                                isOpen = !innerView.isOpen,
                                innerViewContents = if (!innerView.isOpen && innerView.innerViewContents.isEmpty()) {
                                    getInnerViewContentUseCase(id)
                                        .first().interviewGroups.mapNotNull { interviewGroup ->
                                            if (!interviewGroup.isRecording) {
                                                InterviewGroupSelectUiState.create(
                                                    innerView.innerViewId,
                                                    interviewGroup
                                                )
                                            } else {
                                                null
                                            }
                                        }.toPersistentList()
                                } else {
                                    innerView.innerViewContents
                                }
                            )
                        } else {
                            innerView
                        }
                    }.toPersistentList()
                )
            }
        }
    }

    fun selectInterviewGroupItem(innerViewId: String, interviewGroupId: Int) {
        viewModelScope.launch {
            _mediaAddUiState.update {
                it.copy(
                    innerViews = it.innerViews.map { innerView ->
                        if (innerView.innerViewId == innerViewId) {
                            innerView.copy(
                                innerViewContents = innerView.innerViewContents.map { interviewGroup ->
                                    if (interviewGroup.interviewGroupId == interviewGroupId) {
                                        interviewGroup.copy(
                                            isOpen = !interviewGroup.isOpen,
                                            interviewContents = if (!interviewGroup.isOpen && interviewGroup.interviewContents.isEmpty()) {
                                                getInterviewGroupContentUseCase(
                                                    innerViewId,
                                                    interviewGroupId
                                                )
                                                    .first().interviews.map { interview ->
                                                        InterviewSelectUiState.create(
                                                            innerViewId,
                                                            interviewGroupId,
                                                            interview
                                                        )
                                                    }.toPersistentList()
                                            } else {
                                                interviewGroup.interviewContents
                                            }
                                        )
                                    } else {
                                        interviewGroup
                                    }
                                }.toPersistentList()
                            )
                        } else {
                            innerView
                        }
                    }.toPersistentList()
                )
            }
        }
    }

    fun selectInterviewItem(innerViewId: String, interviewGroupId: Int, innerProjectId: Int) {
        viewModelScope.launch {
            val selectedInnerProjectId =
                _mediaAddUiState.value.selectedInnerProjectId.toMutableList()
            val innerViews = _mediaAddUiState.value.innerViews.map { innerView ->
                if (innerView.innerViewId == innerViewId) {
                    innerView.copy(
                        innerViewContents = innerView.innerViewContents.map { interviewGroup ->
                            if (interviewGroup.interviewGroupId == interviewGroupId) {
                                interviewGroup.copy(
                                    interviewContents = interviewGroup.interviewContents.map { interview ->
                                        if (interview.innerProjectId == innerProjectId) {
                                            if (!interview.selected) {
                                                selectedInnerProjectId.add(interview.innerProjectId)
                                            } else {
                                                selectedInnerProjectId.remove(interview.innerProjectId)
                                            }
                                            interview.copy(
                                                selected = !interview.selected
                                            )
                                        } else {
                                            interview
                                        }
                                    }.toPersistentList()
                                )
                            } else {
                                interviewGroup
                            }
                        }.toPersistentList()
                    )
                } else {
                    innerView
                }
            }.toPersistentList()

            _mediaAddUiState.update {
                it.copy(
                    selectedInnerProjectId = selectedInnerProjectId.toPersistentList(),
                    innerViews = innerViews
                )
            }
        }
    }

    fun addInterviewItem() {
        viewModelScope.launch {
            val newMediaItems =
                _mediaAddUiState.value.selectedInnerProjectId.map { innerProjectId ->
                    getInnerProjectByIdUseCase(innerProjectId).first()
                }.flatMap {
                    it.innerProjectComponents.media
                }.map {
                    MediaUiState(
                        medium = it
                    )
                }

            _editUiState.update {
                it.copy(
                    media = it.media.toPersistentList().addAll(newMediaItems)
                )
            }

            fetchPlayer()

            _mediaAddUiState.update {
                it.copy(
                    isOpen = false,
                    selectedInnerProjectId = persistentListOf(),
                    innerViews = persistentListOf()
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackStateListener.cancelJob()
        player.release()
    }
}