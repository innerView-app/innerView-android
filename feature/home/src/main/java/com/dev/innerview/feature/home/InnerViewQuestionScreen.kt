package com.dev.innerview.feature.home

import android.content.res.Configuration
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.feature.home.component.InnerViewQuestionItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
internal fun InnerViewQuestionScreen(
    padding: PaddingValues,
    innerViewId: String,
    onBackClick: () -> Unit,
    viewModel: InnerViewQuestionViewModel = hiltViewModel()
) {
    val innerViewQuestionUiState by viewModel.innerViewQuestionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(innerViewId) {
        viewModel.fetchInnerViewQuestions(innerViewId)
    }

    InnerViewQuestionContent(
        padding = padding,
        onBackClick = onBackClick,
        title = innerViewQuestionUiState.title,
        questions = innerViewQuestionUiState.interviewQuestions,
        updateQuestions = { oldIndex, newIndex ->
            viewModel.updateQuestions(innerViewId, oldIndex, newIndex)
        }
    )
}

@Composable
private fun InnerViewQuestionContent(
    padding: PaddingValues,
    onBackClick: () -> Unit,
    title: String,
    questions: ImmutableList<String>,
    updateQuestions: (Int, Int) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = title,
            navigationType = TopAppBarNavigationType.Back,
            onNavigationClick = { onBackClick() }
        )
        Column(
            modifier = Modifier
                .padding(top = appBarSize)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                Modifier.padding(Paddings.xlarge)
            ) {
                Text(
                    text = stringResource(R.string.feature_home_innerview_question_list_title),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.feature_home_innerview_question_description),
                    style = MaterialTheme.typography.labelMedium
                )

            }
            if (questions.isNotEmpty()) {
                ReorderableList(
                    questions = questions,
                    updateQuestions = updateQuestions
                )
            }
        }
    }
}

@Composable
private fun ReorderableList(
    questions: ImmutableList<String>,
    updateQuestions: (Int, Int) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val offsetYs = remember { questions.map { 0 }.toMutableStateList() }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var draggedOffsetY by remember { mutableIntStateOf(0) }
    var targetIndex by remember { mutableIntStateOf(0) }

    LazyColumn(
        Modifier.fillMaxSize(),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy((-1).dp)
    ) {
        itemsIndexed(questions, key = { _, question -> question }) { id, question ->
            val isDragged = id == draggedIndex
            val overlapPx = with(LocalDensity.current) { 1.dp.toPx() }.roundToInt()
            val animatedOffsetY by animateIntAsState(
                targetValue = offsetYs[id],
                label = "moving animation"
            )

            InnerViewQuestionItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        if (isDragged) IntOffset(0, draggedOffsetY)
                        else if (draggedIndex == null) IntOffset(0, 0)
                        else IntOffset(0, animatedOffsetY)
                    }
                    .zIndex(zIndex = if (isDragged) 1f else 0f)
                    .background(
                        if (isDragged) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.background
                    ),
                onDragStarted = {
                    draggedIndex = id
                    targetIndex = id
                },
                onDragStopped = {
                    draggedIndex = null
                    draggedOffsetY = 0
                    updateQuestions(id, targetIndex)
                    offsetYs.fill(0)
                },
                draggableState = rememberDraggableState { dragAmount ->
                    val itemHeight = lazyListState.layoutInfo.visibleItemsInfo[0].size - overlapPx
                    val min = (lazyListState.firstVisibleItemIndex - id) * itemHeight
                    val max = min + lazyListState.layoutInfo.visibleItemsInfo.lastIndex * itemHeight

                    draggedOffsetY = (draggedOffsetY + dragAmount.roundToInt()).coerceIn(min, max)

                    val tmpOffsetY = draggedOffsetY - offsetYs[id]
                    if (tmpOffsetY.absoluteValue > itemHeight / 2) {
                        if (tmpOffsetY > 0) {
                            targetIndex++
                            if (targetIndex > id) offsetYs[targetIndex] -= itemHeight
                            else offsetYs[targetIndex - 1] -= itemHeight
                            offsetYs[id] += itemHeight
                        } else {
                            targetIndex--
                            if (targetIndex < id) offsetYs[targetIndex] += itemHeight
                            else offsetYs[targetIndex + 1] += itemHeight
                            offsetYs[id] -= itemHeight
                        }
                    }
                },
                content = question
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InnerViewQuestionScreenPreview() {
    InnerViewTheme {
        InnerViewQuestionContent(
            padding = PaddingValues(),
            onBackClick = {},
            title = "title",
            questions = (1..20).map { it.toString() }.toPersistentList(),
            updateQuestions = { _, _ -> }
        )
    }
}
