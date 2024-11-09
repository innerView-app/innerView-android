package com.dev.innerview.feature.record.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings

@Composable
fun CameraOptionSingleButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    iconDescription: String? = null,
    isScrim: Boolean = true,
    onClick: () -> Unit,
) {

    val scrimColor = if (isScrim) {
        MaterialTheme.colorScheme.scrim
    } else {
        Color.Transparent
    }

    IconButton(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp)),
        onClick = { onClick() },
        colors = IconButtonColors(
            contentColor = MaterialTheme.colorScheme.onTertiary,
            containerColor = scrimColor,
            disabledContentColor = MaterialTheme.colorScheme.onTertiary,
            disabledContainerColor = scrimColor
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = iconDescription
        )
    }
}

@Composable
fun CameraOptionMultiButton(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.scrim)
            .padding(vertical = Paddings.small),
        verticalArrangement = Arrangement.spacedBy(Paddings.medium)
    ) {
        content()
    }
}

@Composable
fun ScrimText(
    modifier: Modifier = Modifier,
    text: String = "",
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.scrim)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = Paddings.large, vertical = Paddings.small),
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onTertiary
            )
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun CameraOptionSingleButtonPreview() {
    InnerViewTheme {
        CameraOptionSingleButton(
            imageVector = Icons.Filled.Add,
            onClick = {}
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun CameraOptionMultiButtonPreview() {
    InnerViewTheme {
        CameraOptionMultiButton(
            modifier = Modifier
        ) {
            CameraOptionSingleButton(
                imageVector = Icons.Filled.Cameraswitch,
                isScrim = false,
                onClick = {}
            )

            CameraOptionSingleButton(
                imageVector = Icons.Filled.FlashOn,
                isScrim = false,
                onClick = {}
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ScrimTextPreview() {
    InnerViewTheme {
        ScrimText(
            text = "00:00"
        )
    }
}