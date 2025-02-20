package com.dev.innerview.feature.edit.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.component.getScaledVideoThumbnailByPath
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.core.model.Subtitle
import com.dev.innerview.feature.edit.model.MediaItemSide
import com.dev.innerview.feature.edit.model.MediaUiState

@Composable
fun MediaItem(
    modifier: Modifier = Modifier,
    mediaUiState: MediaUiState,
    height: Dp = 50.dp,
    updateMediaItemLengthen: (MediaItemSide, Int) -> Unit,
) {
    val thumbnail = getScaledVideoThumbnailByPath(mediaUiState.medium.filePath, height)
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (mediaUiState.selected) {
                        Modifier.border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            RepeatImage(
                modifier = Modifier.fillMaxSize(),
                image = thumbnail,
                containerHeight = height
            )
        }
        if (mediaUiState.selected) {
            Column(
                modifier = Modifier
                    .width(6.dp)
                    .height(height)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.CenterStart)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            updateMediaItemLengthen(MediaItemSide.START, dragAmount.x.toInt())
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically)
            ) {
                Icon(
                    modifier = Modifier.size(4.dp),
                    imageVector = Icons.Filled.Circle,
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier.size(4.dp),
                    imageVector = Icons.Filled.Circle,
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier.size(4.dp),
                    imageVector = Icons.Filled.Circle,
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier
                    .width(6.dp)
                    .height(height)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.CenterEnd)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            updateMediaItemLengthen(MediaItemSide.END, dragAmount.x.toInt())
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically)
            ) {
                Icon(
                    modifier = Modifier.size(4.dp),
                    imageVector = Icons.Filled.Circle,
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier.size(4.dp),
                    imageVector = Icons.Filled.Circle,
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier.size(4.dp),
                    imageVector = Icons.Filled.Circle,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun SubtitleItem(
    modifier: Modifier = Modifier,
    subtitle: Subtitle,
    height: Dp = 50.dp,
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(
                    MaterialTheme.colorScheme.tertiary.copy(
                        alpha = 0.6f
                    )
                )
        )
        Text(
            modifier = Modifier.height(20.dp),
            text = subtitle.text,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun RepeatImage(
    image: ImageBitmap,
    containerHeight: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.height(containerHeight)) {
        var xOffset = 0f
        while (xOffset < size.width) {
            drawImage(
                image = image,
                topLeft = Offset(xOffset, 0f)
            )
            xOffset += image.width
        }
    }
}

@Preview
@Composable
fun MediaItemPreview() {
    InnerViewTheme {
        MediaItem(
            modifier = Modifier.width(100.dp),
            mediaUiState = MediaUiState(
                medium = InterviewPiece(
                    filePath = "",
                    startPosition = 0,
                    endPosition = 0,
                    duration = 0,
                ),
                selected = true,
            ),
            updateMediaItemLengthen = { _, _ -> },
        )
    }
}

@Preview
@Composable
fun SubtitleItemPreview() {
    InnerViewTheme {
        SubtitleItem(
            modifier = Modifier.width(100.dp),
            subtitle = Subtitle(
                text = "abcaaaaaaaaaaaaaa"
            )
        )
    }
}