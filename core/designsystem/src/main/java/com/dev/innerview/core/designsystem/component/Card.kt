package com.dev.innerview.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings

@Composable
fun InnerViewCard(
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = color,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp,
        content = content,
    )
}

@Composable
fun InterviewGroupCard(
    modifier: Modifier = Modifier,
    filePath: String?,
    interviewDescription: String? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            VideoThumbnail(
                modifier = Modifier.fillMaxSize(),
                filePath = "interviews/$filePath",
                contentDescription = interviewDescription
            )

            content()
        }
    }
}

@Composable
fun InterviewCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    filePath: String?,
    interviewDescription: String? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp,
    ) {

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                VideoThumbnail(
                    modifier = Modifier,
                    filePath = "$filePath",
                    contentScale = ContentScale.FillWidth,
                    contentDescription = interviewDescription
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colorStops = arrayOf(
                                    0.65f to Color.Transparent,
                                    1f to color
                                )
                            )
                        )
                        .align(Alignment.CenterEnd)
                )
            }
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize()
            ) {
                content()
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun InnerViewCardPreview() {
    InnerViewTheme {
        InnerViewCard(modifier = Modifier.size(320.dp, 160.dp), content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "headlineLarge", style = MaterialTheme.typography.headlineLarge)
                Text(text = "bodyLarge", style = MaterialTheme.typography.bodyLarge)
            }
        })
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun InterviewGroupCardPreview() {
    InnerViewTheme {
        InterviewGroupCard(
            modifier = Modifier.size(150.dp, 200.dp),
            filePath = ""
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = Paddings.medium, bottom = Paddings.medium)
            ) {
                OutlinedText(
                    text = "30개의 질문",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onTertiary
                    ),
                    outlineColor = MaterialTheme.colorScheme.tertiary,
                    outlineDrawStyle = Stroke(
                        width = 4f
                    )
                )
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedText(
                    text = "2024.08.25",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onTertiary
                    ),
                    outlineColor = MaterialTheme.colorScheme.tertiary,
                    outlineDrawStyle = Stroke(
                        width = 5f
                    )
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun InterviewCardPreview() {
    InnerViewTheme {
        InterviewCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            filePath = ""
        ) {
            OutlinedText(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .padding(horizontal = Paddings.large),
                text = "오늘 날씨는 어땠나요?",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                outlineColor = MaterialTheme.colorScheme.tertiary,
                outlineDrawStyle = Stroke(
                    width = 4f
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
