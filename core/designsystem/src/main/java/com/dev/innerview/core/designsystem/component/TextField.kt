package com.dev.innerview.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings

@Composable
fun InnerViewDialogTextField(
    modifier: Modifier = Modifier,
    value: () -> String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    placeholderText: String = "",
    labelText: String = "",
    supportingText: String = "",
) {
    BasicTextField(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.extraSmall
            )
            .fillMaxWidth(),
        value = value(),
        onValueChange = { onValueChange(it) },
        singleLine = singleLine,
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodySmall,
        decorationBox = @Composable { innerTextField ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(bottom = Paddings.medium),
                    text = labelText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value().isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        )
                    }
                    innerTextField()
                }
                HorizontalDivider()
                Text(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = Paddings.xsmall),
                    text = supportingText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                )
            }
        }
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InnerViewTextFieldPreview() {
    InnerViewTheme {

        var text by remember { mutableStateOf("") }

        InnerViewDialogTextField(
            value = { text },
            onValueChange = { text = it },
            placeholderText = "placeholderText",
            labelText = "labelText",
            supportingText = "supportingText"
        )
    }
}
