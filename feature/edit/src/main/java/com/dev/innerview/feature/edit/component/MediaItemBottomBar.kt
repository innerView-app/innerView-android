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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.dev.innerview.feature.edit.model.PositionUpdateOption
import com.dev.innerview.feature.edit.model.SplitOption
import kotlinx.coroutines.launch

@Composable
fun MediaItemBottomBar(
    isVisible: Boolean = false,
    modifier: Modifier = Modifier,
    cancelMediaItem: () -> Unit,
    splitMediaItem: (SplitOption) -> Unit,
    deleteMediaItem: () -> Unit,
    updateMediaItemPosition: (PositionUpdateOption) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })

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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "OptionPrev",
                    tint = MaterialTheme.colorScheme.surfaceContainer
                )
                HorizontalPager(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.onSurface),
                    state = pagerState
                ) { page ->
                    when (page) {
                        0 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth()
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
                        }

                        1 -> {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = { updateMediaItemPosition(PositionUpdateOption.START) }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                                            contentDescription = "ContentPositionChangeToStart",
                                            tint = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                        Text(
                                            modifier = Modifier.padding(top = Paddings.xsmall),
                                            text = "맨 앞으로",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                    }
                                }
                                IconButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = { updateMediaItemPosition(PositionUpdateOption.LEFT) }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(24.dp),
                                            imageVector = Icons.Filled.ChevronLeft,
                                            contentDescription = "ContentPositionChangeToLeft",
                                            tint = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                        Text(
                                            modifier = Modifier.padding(top = Paddings.xsmall),
                                            text = "왼쪽으로",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                    }
                                }
                                IconButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = { updateMediaItemPosition(PositionUpdateOption.RIGHT) }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ChevronRight,
                                            contentDescription = "ContentPositionChangeToRight",
                                            tint = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                        Text(
                                            modifier = Modifier.padding(top = Paddings.xsmall),
                                            text = "오른쪽으로",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                    }
                                }
                                IconButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = { updateMediaItemPosition(PositionUpdateOption.END) }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(24.dp),
                                            imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                                            contentDescription = "ContentPositionChangeToEnd",
                                            tint = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                        Text(
                                            modifier = Modifier.padding(top = Paddings.xsmall),
                                            text = "맨 뒤로",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Icon(
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "OptionNext",
                    tint = MaterialTheme.colorScheme.surfaceContainer
                )
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
            updateMediaItemPosition = {},
        )
    }
}