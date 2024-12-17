package com.dev.innerview.feature.edit.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.component.getScaledVideoThumbnailByPath
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.core.model.Subtitle

@Composable
fun MediaItem(
    modifier: Modifier = Modifier,
    videoItem: InterviewPiece,
    height: Dp = 50.dp,
) {
    val thumbnail = getScaledVideoThumbnailByPath(videoItem.filePath, height)
    Surface(
        modifier = modifier
            .height(height),
        color = MaterialTheme.colorScheme.tertiary,
        shape = RoundedCornerShape(8.dp),
    ) {
        RepeatImage(
            image = thumbnail,
            containerHeight = height
        )
    }
}

@Composable
fun SubtitleItem(
    modifier: Modifier = Modifier,
    subtitle: Subtitle,
    height: Dp = 50.dp,
) {
    Column(
        modifier = modifier.width(100.dp)
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
            videoItem = InterviewPiece(
                filePath = "",
                startPosition = 0,
                endPosition = 0,
                duration = 0,
            )
        )
    }
}

@Preview
@Composable
fun SubtitleItemPreview() {
    InnerViewTheme {
        SubtitleItem(
            subtitle = Subtitle(
                text = "abcaaaaaaaaaaaaaa"
            )
        )
    }
}