package com.dev.innerview.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositionSeekBar(
    modifier: Modifier = Modifier,
    position: Long,
    duration: Long,
    onPositionChange: (Long) -> Unit,
    onPositionChangeFinished: () -> Unit = {},
    colors: SliderColors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.primary,
        activeTrackColor = MaterialTheme.colorScheme.primary,
        inactiveTrackColor = MaterialTheme.colorScheme.background
    )
) {
    Slider(
        modifier = modifier,
        value = position.toFloat(),
        onValueChange = {
            onPositionChange(it.toLong())
        },
        onValueChangeFinished = onPositionChangeFinished,
        valueRange = 0F..duration.toFloat().coerceAtLeast(0F),
        thumb = {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        },
        track = { sliderPositions ->
            val trackHeight = 4.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .background(colors.inactiveTrackColor, RoundedCornerShape(trackHeight / 2))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(sliderPositions.value / sliderPositions.valueRange.endInclusive)
                    .height(trackHeight)
                    .background(colors.activeTrackColor, RoundedCornerShape(trackHeight / 2))
            )
        }
    )
}