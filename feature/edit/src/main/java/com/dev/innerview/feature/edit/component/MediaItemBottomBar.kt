package com.dev.innerview.feature.edit.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.feature.edit.R
import com.dev.innerview.feature.edit.model.SplitOption

@Composable
fun MediaItemBottomBar(
    isVisible: Boolean = false,
    modifier: Modifier = Modifier,
    cancelMediaItem: () -> Unit,
    splitMediaItem: (SplitOption) -> Unit,
    deleteMediaItem: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        label = "mediaItemBottomBarAnimate"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSurface)
            ) {
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = { deleteMediaItem() }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "ContentCut",
                            tint = MaterialTheme.colorScheme.surfaceContainer
                        )
                        Text(
                            modifier = Modifier.padding(top = Paddings.xsmall),
                            text = "삭제",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                }
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = { splitMediaItem(SplitOption.LEFT) }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.left_cut),
                            contentDescription = "ContentCut",
                            tint = MaterialTheme.colorScheme.surfaceContainer
                        )
                        Text(
                            modifier = Modifier.padding(top = Paddings.xsmall),
                            text = "현재부터 자르기",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                }
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = { splitMediaItem(SplitOption.NONE) }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCut,
                            contentDescription = "ContentCut",
                            tint = MaterialTheme.colorScheme.surfaceContainer
                        )
                        Text(
                            modifier = Modifier.padding(top = Paddings.xsmall),
                            text = "분할",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                }
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = { splitMediaItem(SplitOption.RIGHT) }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.right_cut),
                            contentDescription = "ContentCut",
                            tint = MaterialTheme.colorScheme.surfaceContainer
                        )
                        Text(
                            modifier = Modifier.padding(top = Paddings.xsmall),
                            text = "현재까지 자르기",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .clickable { cancelMediaItem() }
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = Paddings.large),
                    text = "완료",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun MediaItemBottomBarPreview() {
    InnerViewTheme {
        MediaItemBottomBar(
            isVisible = true,
            cancelMediaItem = {},
            splitMediaItem = {},
            deleteMediaItem = {},
        )
    }
}