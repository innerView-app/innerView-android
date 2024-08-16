package com.dev.innerview.core.designsystem.component

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dev.innerview.core.designsystem.theme.InnerViewTheme

@Composable
fun InnerViewFloatingActionButton(
    iconImageVector: ImageVector,
    iconDescription: String? = null,
    @StringRes textRes: Int? = null,
    textString: String = "",
) {
    ExtendedFloatingActionButton(
        text = {
            Text(
                text = textRes?.let { stringResource(id = it) } ?: textString,
                style = MaterialTheme.typography.labelLarge
            )
        },
        icon = {
            Icon(iconImageVector, iconDescription)
        },
        onClick = { },
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
            textString = "인터뷰 생성"
        )
    }
}
