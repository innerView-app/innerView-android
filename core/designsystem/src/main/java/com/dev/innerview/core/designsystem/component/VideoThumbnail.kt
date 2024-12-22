package com.dev.innerview.core.designsystem.component

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.core.net.toUri
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

@Composable
fun getScaledVideoThumbnailByPath(filePath: String, targetHeight: Dp): ImageBitmap {
    val context = LocalContext.current

    val targetHeightPx = with(LocalDensity.current) { targetHeight.toPx() }

    val bitmap = remember(filePath) {
        val videoFile = File(context.filesDir, filePath)
        var resultBitmap: Bitmap? = null
        if (videoFile.exists()) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, videoFile.toUri())
                resultBitmap = retriever.getFrameAtTime(0)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
            }
        }
        resultBitmap
    } ?: ImageBitmap.imageResource(id = R.drawable.video_thumbnail_error).asAndroidBitmap()

    val scale = targetHeightPx / bitmap.height
    val scaledWidth = (bitmap.width * scale).toInt()
    val scaledHeight = (bitmap.height * scale).toInt()

    return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true).asImageBitmap()
}