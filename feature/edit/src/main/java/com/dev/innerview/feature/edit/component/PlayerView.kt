package com.dev.innerview.feature.edit.component

import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player

@Composable
fun PlayerView(
    modifier: Modifier,
    player: Player?,
) {
    var surfaceView: SurfaceView? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            player?.clearVideoSurfaceView(surfaceView)
        }
    }

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
        modifier = modifier
    )
}