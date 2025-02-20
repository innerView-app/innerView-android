package com.dev.innerview.feature.edit

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.feature.edit.component.EditLayer
import com.dev.innerview.feature.edit.component.MediaItem
import com.dev.innerview.feature.edit.component.MediaItemBottomBar
import com.dev.innerview.feature.edit.component.PlayerBar
import com.dev.innerview.feature.edit.component.PlayerView
import com.dev.innerview.feature.edit.model.EditUiState
import com.dev.innerview.feature.edit.model.MediaItemSide
import com.dev.innerview.feature.edit.model.MediaUiState
import com.dev.innerview.feature.edit.model.SplitOption
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

@Composable
internal fun EditScreen(
    innerProjectId: Int,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onBackClick: () -> Unit,
    viewModel: EditViewModel = hiltViewModel(),
) {

    val editUiState by viewModel.editUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchInnerProject(innerProjectId)
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    EditContent(
        innerProjectId = innerProjectId,
        player = viewModel.player,
        editUiState = editUiState,
        onBackClick = onBackClick,
        addMedia = viewModel::addMediaItem,
        updateZoom = viewModel::updateZoom,
        seekToScrollPosition = viewModel::seekToScrollPosition,
        seekByMediaItem = viewModel::seekByMediaItem,
        seekByPosition = viewModel::seekByPosition,
        selectMediaItem = viewModel::selectMediaItem,
        cancelMediaItem = viewModel::cancelMediaItem,
        splitMediaItem = viewModel::splitMediaItem,
        deleteMediaItem = viewModel::deleteMediaItem,
        updateMediaItemLengthen = viewModel::updateMediaItemLengthen,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EditContent(
    innerProjectId: Int,
    player: Player?,
    editUiState: EditUiState,
    onBackClick: () -> Unit,
    addMedia: () -> Unit,
    updateZoom: (Float) -> Unit,
    seekToScrollPosition: (Int) -> Unit,
    seekByMediaItem: (Int) -> Unit,
    seekByPosition: (Long) -> Unit,
    selectMediaItem: (Int) -> Unit,
    cancelMediaItem: () -> Unit,
    splitMediaItem: (SplitOption) -> Unit,
    deleteMediaItem: () -> Unit,
    updateMediaItemLengthen: (MediaItemSide, Int, Int) -> Unit,
) {
    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        snapshotFlow { scrollState.value }
            .filter { scrollState.isScrollInProgress }
            .collect { scrollPosition ->
                seekToScrollPosition(scrollPosition)
            }
    }

    LaunchedEffect(editUiState.position) {
        if (!scrollState.isScrollInProgress) {
            scrollState.scrollTo((editUiState.position * editUiState.zoom).toInt())
        }
    }


    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = "편집",
            navigationType = TopAppBarNavigationType.Back,
            onNavigationClick = { onBackClick() },
            actionButtons = {
                InnerViewAppBarIcon(
                    imageVector = Icons.Filled.Done,
                    navigationIconContentDescription = null
                )
            }
        )
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .padding(top = appBarSize)
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxSize()
            ) {
                PlayerView(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .aspectRatio(9f / 16f)
                        .background(Color.Gray),
                    player = player,
                )
                IconButton(
                    modifier = Modifier.align(Alignment.BottomStart),
                    onClick = {
                        if (editUiState.isPlaying) {
                            player?.pause()
                        } else {
                            if (player?.playbackState == Player.STATE_ENDED) {
                                player.seekToDefaultPosition(0)
                            }
                            player?.play()

                        }
                    }
                ) {
                    if (editUiState.isPlaying) {
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = null,
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                IconButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = { addMedia() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExposurePlus1,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PlayerBar(
                        editUiState = editUiState,
                        scrollState = scrollState,
                        seekToMediaItem = seekByMediaItem,
                        seekByPosition = seekByPosition,
                    )
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        CompositionLocalProvider(
                            LocalOverscrollConfiguration provides null
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTransformGestures { _, _, zoom, _ ->
                                            updateZoom(zoom)
                                        }
                                    }
                                    .scrollable(
                                        state = scrollState,
                                        orientation = Orientation.Horizontal,
                                        reverseDirection = true
                                    )
                            ) {
                                EditLayer(
                                    scrollState = scrollState,
                                    editUiState = editUiState,
                                    layerIcon = Icons.Filled.AddCircleOutline,
                                    layerIconDescription = "미디어 추가",
                                    layerName = "Media",
                                    onLayerIconClick = { }
                                ) {
                                    for (i in 0 until editUiState.media.size) {
                                        val duration =
                                            with(editUiState.media[i]) { medium.endPosition - medium.startPosition }
                                        val widthDp =
                                            with(density) { (duration * editUiState.zoom).toDp() }
                                        val startOffset =
                                            with(density) { (editUiState.accumulatedDurations[i] * editUiState.zoom).toDp() }
                                        MediaItem(
                                            modifier = Modifier
                                                .width(widthDp)
                                                .padding(vertical = 2.dp)
                                                .offset(x = startOffset)
                                                .clickable { selectMediaItem(i) },
                                            mediaUiState = editUiState.media[i],
                                            updateMediaItemLengthen = { side, value ->
                                                updateMediaItemLengthen(side, i, value)
                                            }
                                        )
                                    }
                                }
                                EditLayer(
                                    scrollState = scrollState,
                                    editUiState = editUiState,
                                    layerIcon = Icons.Filled.TextFields,
                                    layerIconDescription = "자막 추가",
                                    layerName = "Text",
                                    onLayerIconClick = { }
                                ) {
//                                    Box(
//                                        modifier = Modifier
//                                            .height(50.dp)
//                                            .width(30.dp)
//                                            .background(Color.Cyan)
//                                    )
                                }
                            }
                        }
                    }
                }
                VerticalDivider(
                    modifier = Modifier.align(Alignment.Center),
                    thickness = 1.dp,
                    color = Color.Red
                )
                MediaItemBottomBar(
                    isVisible = editUiState.media.any { it.selected },
                    modifier = Modifier.align(Alignment.BottomCenter),
                    cancelMediaItem = cancelMediaItem,
                    splitMediaItem = splitMediaItem,
                    deleteMediaItem = deleteMediaItem,
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun EditContentPreview() {
    InnerViewTheme {
        EditContent(
            innerProjectId = 0,
            editUiState = EditUiState(
                isPlaying = true,
                duration = 6000L,
                zoom = 1f,
                media = persistentListOf(
                    MediaUiState(
                        medium = InterviewPiece(
                            filePath = "",
                            startPosition = 0L,
                            endPosition = 5000L,
                            duration = 5000L,
                        ),
                        selected = false,
                    ),
                    MediaUiState(
                        medium = InterviewPiece(
                            filePath = "",
                            startPosition = 1000L,
                            endPosition = 2000L,
                            duration = 3000L,
                        ),
                        selected = false,
                    )
                ),
                accumulatedDurations = persistentListOf(
                    0L, 5000L, 6000L
                )
            ),
            player = null,
            onBackClick = {},
            addMedia = {},
            updateZoom = {},
            seekToScrollPosition = {},
            seekByMediaItem = {},
            seekByPosition = {},
            selectMediaItem = {},
            cancelMediaItem = {},
            splitMediaItem = {},
            deleteMediaItem = {},
            updateMediaItemLengthen = { _, _, _ -> },
        )
    }
}