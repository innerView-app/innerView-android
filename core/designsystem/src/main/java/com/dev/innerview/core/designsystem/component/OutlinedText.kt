package com.dev.innerview.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.dev.innerview.core.designsystem.theme.InnerViewTheme

@Composable
fun OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    outlineColor: Color,
    outlineDrawStyle: Stroke = Stroke(),
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    Box(modifier = modifier) {
        Text(
            text = text,
            color = outlineColor,
            textDecoration = null,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style.copy(
                drawStyle = outlineDrawStyle,
            ),
        )

        Text(
            text = text,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style,
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun OutlinedTextPreview() {
    InnerViewTheme {
        OutlinedText(
            text = "Outlined Text",
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onTertiary
            ),
            outlineColor = MaterialTheme.colorScheme.tertiary,
            outlineDrawStyle = Stroke(
                width = 4f
            )
        )
    }
}