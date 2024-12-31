package com.dev.innerview.feature.record.component

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.model.RecordState

@Composable
fun RecordButton(
    modifier: Modifier = Modifier,
    recordState: RecordState,
    onRecordStart: () -> Unit,
    onRecordPause: () -> Unit,
    onRecordResume: () -> Unit,
) {
    val cornerSize by animateDpAsState(
        targetValue = if (recordState == RecordState.IDLE || recordState == RecordState.PAUSE) {
            50.dp
        } else {
            6.dp
        },
        label = "cornerSize Animation"
    )

    val outerBorderPadding by animateDpAsState(
        targetValue = if (recordState == RecordState.IDLE || recordState == RecordState.PAUSE) {
            0.dp
        } else {
            5.dp
        },
        label = "outerBorderPadding Animation"
    )

    val innerBoxSize by animateDpAsState(
        targetValue = if (recordState == RecordState.IDLE || recordState == RecordState.PAUSE) {
            60.dp
        } else {
            40.dp
        },
        label = "innerBoxSize Animation"
    )

    Box(
        modifier = modifier
            .size(80.dp)
            .padding(outerBorderPadding)
            .border(
                width = 4.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .clickable {
                when (recordState) {
                    RecordState.IDLE -> {
                        onRecordStart()
                    }

                    RecordState.RECORDING -> {
                        onRecordPause()
                    }

                    else -> {
                        onRecordResume()
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .size(innerBoxSize)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(cornerSize))
                .background(Color(0xFFE74E45))
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun RecordButtonPreview() {
    InnerViewTheme {
        var recordState by remember { mutableStateOf(RecordState.IDLE) }

        RecordButton(
            recordState = recordState,
            onRecordStart = {
                recordState = RecordState.RECORDING
            },
            onRecordPause = {
                recordState = RecordState.PAUSE
            },
            onRecordResume = {
                recordState = RecordState.RECORDING
            }
        )
    }
}