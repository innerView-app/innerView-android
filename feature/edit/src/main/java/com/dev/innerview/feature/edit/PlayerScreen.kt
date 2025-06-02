package com.dev.innerview.feature.edit

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.PositionSeekBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.feature.edit.component.PlayerView
import com.dev.innerview.feature.edit.model.PlayerUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun PlayerScreen(
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {

    val playerUiState by viewModel.playerUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    PlayerContent(
        player = viewModel.player,
        playerUiState = playerUiState,
        onBackClick = onBackClick,
        seekToPosition = viewModel::seekToPosition
    )
}

@Composable
internal fun PlayerContent(
    player: Player?,
    playerUiState: PlayerUiState,
    onBackClick: () -> Unit,
    seekToPosition: (Long) -> Unit,
) {
    var latestPlayerState: Boolean? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = playerUiState.title,
            navigationType = TopAppBarNavigationType.Back,
            onNavigationClick = { onBackClick() }
        )

        Column(
            modifier = Modifier
                .padding(top = appBarSize)
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            InnerProjectPlayer(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                playerUiState = playerUiState,
                player = player
            )

            PlayerController(
                modifier = Modifier,
                title = playerUiState.title,
                position = playerUiState.position,
                duration = playerUiState.duration,
                onPositionChange = {
                    if (latestPlayerState == null) {
                        latestPlayerState = playerUiState.isPlaying
                    }
                    seekToPosition(it)
                },
                onPositionChangeFinished = {
                    if (latestPlayerState == true) {
                        player?.play()
                    }
                    latestPlayerState = null
                }
            )
        }
    }
}

@Composable
fun InnerProjectPlayer(
    modifier: Modifier = Modifier,
    playerUiState: PlayerUiState,
    player: Player?
) {
    var showPlayStateIcon by remember { mutableStateOf(false) }

    LaunchedEffect(showPlayStateIcon) {
        if (showPlayStateIcon) {
            delay(500L)
        }
        showPlayStateIcon = false
    }

    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            if (playerUiState.isPlaying) {
                player?.pause()
            } else {
                if (player?.playbackState == Player.STATE_ENDED) {
                    player.seekToDefaultPosition(0)
                }
                player?.play()
            }
            showPlayStateIcon = true
        },
        contentAlignment = Alignment.Center
    ) {
        PlayerView(
            modifier = Modifier
                .aspectRatio(9f / 16f)
                .background(Color.Gray),
            player = player,
        )

        AnimatedVisibility(
            visible = showPlayStateIcon,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = if (playerUiState.isPlaying) {
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
    }
}

@SuppressLint("DefaultLocale")
@Composable
internal fun PlayerController(
    modifier: Modifier = Modifier,
    title: String,
    position: Long,
    duration: Long,
    onPositionChange: (Long) -> Unit,
    onPositionChangeFinished: () -> Unit,
    colors: SliderColors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.primary,
        activeTrackColor = MaterialTheme.colorScheme.primary,
        inactiveTrackColor = MaterialTheme.colorScheme.background
    )
) {
    val positionMinutes = position / 60000
    val positionSeconds = (position % 60000) / 1000
    val durationMinutes = duration / 60000
    val durationSeconds = (duration % 60000) / 1000

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondary)
            .padding(Paddings.medium)
            .padding(vertical = Paddings.large),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.padding(bottom = Paddings.large),
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(end = Paddings.medium),
                style = MaterialTheme.typography.bodySmall,
                text = String.format("%02d:%02d", positionMinutes, positionSeconds),
            )

            PositionSeekBar(
                modifier = Modifier.weight(1f),
                position = position,
                duration = duration,
                onPositionChange = onPositionChange,
                onPositionChangeFinished = onPositionChangeFinished,
                colors = colors,
            )

            Text(
                modifier = Modifier.padding(start = Paddings.medium),
                style = MaterialTheme.typography.bodySmall,
                text = String.format("%02d:%02d", durationMinutes, durationSeconds),
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PlayerContentPreview() {
    InnerViewTheme {
        PlayerContent(
            player = null,
            playerUiState = PlayerUiState(
                title = "sample title",
                duration = 10000,
                position = 4000
            ),
            onBackClick = {},
            seekToPosition = {}
        )
    }
}