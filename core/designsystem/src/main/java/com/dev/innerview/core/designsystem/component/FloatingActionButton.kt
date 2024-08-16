package com.dev.innerview.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.dev.innerview.core.designsystem.theme.InnerViewTheme

@Composable
fun InnerViewFloatingActionButton(
    modifier: Modifier = Modifier,
    iconImageVector: ImageVector,
    iconDescription: String? = null,
    text: String = "",
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        },
        icon = {
            Icon(iconImageVector, iconDescription)
        },
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun InnerViewFloatingActionButtonPreview() {
    InnerViewTheme {
        InnerViewFloatingActionButton(
            iconImageVector = Icons.Filled.Add,
            text = "인터뷰 생성",
            onClick = {}
        )
    }
}
