package com.dev.innerview.feature.peek

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.feature.peek.component.PeekView
import com.dev.innerview.feature.peek.model.PeekUiState
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun PeekRoute(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    viewModel: PeekViewModel = hiltViewModel(),
) {

    val peekUiState by viewModel.peekUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    PeekScreen(
        padding = padding,
        peekUiState = peekUiState,
        fetchNextPeek = viewModel::fetchNextPeek,
    )
}

@Composable
private fun PeekScreen(
    padding: PaddingValues,
    peekUiState: PeekUiState,
    fetchNextPeek: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val pagerState =
        rememberPagerState(initialPage = peekUiState.page, pageCount = { peekUiState.media.size })

    val playerPair = remember {
        Triple(
            ExoPlayer.Builder(context).build(),
            ExoPlayer.Builder(context).build(),
            ExoPlayer.Builder(context).build()
        )
    }

    LaunchedEffect(pagerState.settledPage) {
        controlPlayer(pagerState.settledPage % 3, playerPair)
        if (peekUiState.media.size - pagerState.settledPage <= 1) {
            fetchNextPeek()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                controlPlayer(pagerState.settledPage % 3, playerPair)
            } else if (event == Lifecycle.Event.ON_STOP) {
                controlPlayer(-1, playerPair)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            playerPair.first.release()
            playerPair.second.release()
            playerPair.third.release()
        }
    }

    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (peekUiState.media.isNotEmpty()) {
            VerticalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState
            ) { page ->

                val currentPlayer = when (page % 3) {
                    0 -> playerPair.first
                    1 -> playerPair.second
                    else -> playerPair.third
                }

                PeekView(
                    modifier = Modifier.fillMaxSize(),
                    player = currentPlayer,
                    mediaUiState = peekUiState.media[page],
                )
            }
        } else {
            CircularProgressIndicator()
        }
    }
}

private fun controlPlayer(settledPage: Int, playerPair: Triple<ExoPlayer, ExoPlayer, ExoPlayer>) {
    when (settledPage) {
        0 -> {
            playerPair.first.play()
            playerPair.second.pause()
            playerPair.third.pause()
        }

        1 -> {
            playerPair.first.pause()
            playerPair.second.play()
            playerPair.third.pause()
        }

        2 -> {
            playerPair.first.pause()
            playerPair.second.pause()
            playerPair.third.play()
        }

        else -> {
            playerPair.first.pause()
            playerPair.second.pause()
            playerPair.third.pause()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PeekScreenPreview() {
    InnerViewTheme {
        PeekScreen(
            padding = PaddingValues(),
            peekUiState = PeekUiState(),
            fetchNextPeek = {},
        )
    }
}