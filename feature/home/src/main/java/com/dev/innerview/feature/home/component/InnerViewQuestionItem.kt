package com.dev.innerview.feature.home.component

import android.content.res.Configuration
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import kotlinx.coroutines.CoroutineScope

@Composable
fun InnerViewQuestionItem(
    modifier: Modifier,
    orientation: Orientation = Orientation.Vertical,
    draggableState: DraggableState = DraggableState {},
    onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit = {},
    onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit = {},
    content: String
) {
    Column(modifier = modifier) {
        HorizontalDivider(color = MaterialTheme.colorScheme.tertiary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Paddings.xlarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = content, style = MaterialTheme.typography.labelMedium)
            Icon(
                modifier = Modifier
                    .padding(Paddings.large)
                    .draggable(
                        state = draggableState,
                        orientation = orientation,
                        onDragStarted = onDragStarted,
                        onDragStopped = onDragStopped
                    ),
                imageVector = Icons.Default.Menu,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InnerViewCreateDialogPreview() {
    InnerViewTheme {
        InnerViewQuestionItem(
            modifier = Modifier,
            content = "question content"
        )
    }
}