package com.dev.innerview.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.dev.innerview.core.designsystem.R
import java.io.File

@Composable
fun VideoThumbnail(
    modifier: Modifier = Modifier,
    filePath: String,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {

    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .build()

    val videoFile = File(context.filesDir, filePath)

    val imageRequest = ImageRequest.Builder(context)
        .data(videoFile)
        .placeholder(R.drawable.video_thumbnail_placeholder)
        .error(R.drawable.video_thumbnail_error)
        .build()

    AsyncImage(
        model = imageRequest,
        imageLoader = imageLoader,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}