package com.dev.innerview.feature.peek.component

import android.content.res.Configuration
import android.view.SurfaceView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.dev.innerview.core.designsystem.component.ImageFromUri
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.OutlinedText
import com.dev.innerview.core.designsystem.component.PositionSeekBar
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.feature.peek.model.MediaUiState
import kotlinx.coroutines.delay

@Composable
internal fun PeekView(
    modifier: Modifier,
    player: Player?,
    mediaUiState: MediaUiState,
) {
    var surfaceView: SurfaceView? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(0L) }
    var currentPosition by remember { mutableLongStateOf(0L) }

    var showPlayStateIcon by remember { mutableStateOf(false) }
    var isPlayerUserControl by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val mediaItem = MediaItem.fromUri(mediaUiState.mediaUri)

        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_ONE

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        duration = player.duration.coerceAtLeast(0)
                    }
                }

                override fun onIsPlayingChanged(isPlayingState: Boolean) {
                    isPlaying = isPlayingState
                }
            })
        }

        onDispose {
            player?.stop()
            player?.clearMediaItems()
            player?.clearVideoSurfaceView(surfaceView)
        }
    }

    LaunchedEffect(isPlaying) {
        showPlayStateIcon = true
        while (isPlaying) {
            currentPosition = player?.currentPosition ?: 0L
            delay(10L)
        }
    }

    LaunchedEffect(showPlayStateIcon) {
        if (showPlayStateIcon && isPlayerUserControl) {
            delay(500L)
        }
        showPlayStateIcon = false
        isPlayerUserControl = false
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPlayerUserControl = true
                if (isPlaying) {
                    player?.pause()
                } else {
                    player?.play()
                }
            },
    ) {
        AndroidView(
            factory = {
                SurfaceView(it).apply {
                    surfaceView = this
                }
            },
            update = { view ->
                if (player?.isCommandAvailable(Player.COMMAND_SET_VIDEO_SURFACE) == true) {
                    player.setVideoSurfaceView(view)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = showPlayStateIcon && isPlayerUserControl,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = if (isPlaying) {
                    Icons.Filled.PlayArrow
                } else {
                    Icons.Filled.Pause
                },
                contentDescription = "Playing",
                tint = MaterialTheme.colorScheme.surfaceTint,
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.scrim, shape = CircleShape)
                    .padding(8.dp)
            )
        }

        InnerViewAppBarIcon(
            modifier = Modifier.align(Alignment.TopEnd),
            imageVector = Icons.Filled.MoreVert,
            navigationIconContentDescription = null
        )

        MediaInfo(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = Paddings.xlarge, bottom = Paddings.extra),
            mediaUiState = mediaUiState
        )

        PositionSeekBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = Paddings.medium),
            position = currentPosition,
            duration = duration,
            onPositionChange = {
                currentPosition = it
                player?.seekTo(currentPosition)
            }
        )
    }
}

@Composable
internal fun MediaInfo(
    modifier: Modifier = Modifier,
    mediaUiState: MediaUiState
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageFromUri(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(100.dp)),
                uri = mediaUiState.userProfileUri
            )

            OutlinedText(
                modifier = Modifier.padding(start = Paddings.large),
                text = mediaUiState.userId,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                outlineColor = MaterialTheme.colorScheme.tertiary,
                outlineDrawStyle = Stroke(
                    width = 4f
                )
            )
        }

        OutlinedText(
            modifier = Modifier
                .padding(start = Paddings.large, top = Paddings.large, bottom = Paddings.xlarge),
            text = mediaUiState.title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onTertiary
            ),
            outlineColor = MaterialTheme.colorScheme.tertiary,
            outlineDrawStyle = Stroke(
                width = 4f
            )
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PeekViewPreview() {
    InnerViewTheme {
        PeekView(
            modifier = Modifier.fillMaxSize(),
            player = null,
            mediaUiState = MediaUiState(
                title = "title",
                userId = "userId"
            )
        )
    }
}