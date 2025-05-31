package com.dev.innerview.feature.edit.component

import android.content.res.Configuration
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dev.innerview.core.designsystem.component.InnerViewDialog
import com.dev.innerview.core.designsystem.component.InnerViewRadioButton
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.Scale
import com.dev.innerview.feature.edit.R

@Composable
fun RenderDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: (Scale) -> Unit
) {
    var resolution by remember { mutableStateOf(Scale.SIZE_1080P) }

    InnerViewDialog(
        titleText = stringResource(R.string.feature_edit_render_dialog_title),
        contentText = stringResource(R.string.feature_edit_render_dialog_content),
        confirmText = stringResource(R.string.feature_edit_render_confirm),
        dismissText = stringResource(R.string.feature_edit_render_dismiss),
        onDismissRequest = { onDismissRequest() },
        onConfirmRequest = { onConfirmRequest(resolution) }
    ) {
        Scale.entries.forEachIndexed { i, sizeOption ->
            val typeText = sizeOption.text

            InnerViewRadioButton(
                selected = sizeOption == resolution,
                onClick = { resolution = sizeOption }
            ) {
                Text(
                    text = typeText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (InnerViewType.entries.size - 1 > i) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun RenderDialogPreview() {
    InnerViewTheme {
        RenderDialog(
            onDismissRequest = {},
            onConfirmRequest = {}
        )
    }
}