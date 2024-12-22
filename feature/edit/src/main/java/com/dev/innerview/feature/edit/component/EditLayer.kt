package com.dev.innerview.feature.edit.component

import android.content.res.Configuration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.feature.edit.model.EditUiState
import com.dev.innerview.feature.edit.model.MediaUiState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun EditLayer(
    scrollState: ScrollState,
    editUiState: EditUiState,
    layerName: String,
    layerIcon: ImageVector,
    layerIconDescription: String? = null,
    onLayerIconClick: () -> Unit,
    layerHeight: Dp = 50.dp,
    itemContent: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val widthDp = with(density) { (editUiState.duration * editUiState.zoom).toDp() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(layerHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 1.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f))
                .horizontalScroll(
                    state = scrollState,
                    enabled = false
                ),
        ) {
            Spacer(modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp / 2))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(widthDp)
            ) {
                itemContent()
            }
            Spacer(modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp / 2))
        }
        Column(
            modifier = Modifier
                .height(layerHeight)
                .width(60.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable { onLayerIconClick() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = layerIcon,
                contentDescription = layerIconDescription,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(top = Paddings.xsmall),
                text = layerName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun EditLayerPreview() {
    InnerViewTheme {
        val scrollState = rememberScrollState()
        EditLayer(
            scrollState = scrollState,
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
                    ),
                ),
                accumulatedDurations = persistentListOf(
                    0L, 5000L, 6000L
                )
            ),
            layerName = "media",
            layerIcon = Icons.Filled.AddCircleOutline,
            layerIconDescription = "layerIconDescription",
            onLayerIconClick = {},
        ) {

        }
    }
}