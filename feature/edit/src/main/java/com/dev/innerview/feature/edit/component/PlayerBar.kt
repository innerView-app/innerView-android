package com.dev.innerview.feature.edit.component

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.feature.edit.model.EditUiState
import kotlinx.collections.immutable.persistentListOf

@SuppressLint("DefaultLocale")
@Composable
fun PlayerBar(
    editUiState: EditUiState,
    scrollState: ScrollState,
    seekToMediaItem: (Int) -> Unit,
    seekByPosition: (Long) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "0",
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { seekToMediaItem(-1) }
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { seekByPosition(-10) }
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    val minutes = editUiState.position / 60000
                    val seconds = (editUiState.position % 60000) / 1000
                    val milliseconds = (editUiState.position % 1000) / 10
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 12.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.padding(start = Paddings.xsmall),
                        text = String.format("%02d", milliseconds),
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    val minutes = editUiState.duration / 60000
                    val seconds = (editUiState.duration % 60000) / 1000
                    val milliseconds = (editUiState.duration % 1000) / 10
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 12.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.padding(start = Paddings.xsmall),
                        text = String.format("%02d", milliseconds),
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { seekByPosition(10L) }
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { seekToMediaItem(1) }
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PlayerBarPreview() {
    InnerViewTheme {
        val scrollState = rememberScrollState()
        PlayerBar(
            editUiState = EditUiState(
                isPlaying = true,
                duration = 6000L,
                zoom = 1f,
                media = persistentListOf(
                    InterviewPiece(
                        filePath = "",
                        startPosition = 0L,
                        endPosition = 5000L,
                        duration = 5000L,
                    ),
                    InterviewPiece(
                        filePath = "",
                        startPosition = 1000L,
                        endPosition = 2000L,
                        duration = 3000L,
                    )
                ),
                accumulatedDurations = persistentListOf(
                    0L, 5000L, 6000L
                )
            ),
            scrollState = scrollState,
            seekToMediaItem = {},
            seekByPosition = {},
        )
    }
}