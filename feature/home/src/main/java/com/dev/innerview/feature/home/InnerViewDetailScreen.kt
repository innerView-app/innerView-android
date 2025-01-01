package com.dev.innerview.feature.home

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewAppBarIcon
import com.dev.innerview.core.designsystem.component.InnerViewFloatingActionButton
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.InterviewGroup
import com.dev.innerview.feature.home.component.InterviewGroupItem
import com.dev.innerview.feature.home.model.InnerViewDetailUiEvent
import com.dev.innerview.feature.home.model.InnerViewDetailUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun InnerViewDetailScreen(
    innerViewId: String,
    onBackClick: () -> Unit,
    onShowToast: (text: String) -> Unit,
    navigateToInterviewGroup: () -> Unit,
    navigateToInnerViewQuestion: (String) -> Unit,
    navigateToRecord: (String, Int, String) -> Unit,
    viewModel: InnerViewDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.innerViewDetailUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(innerViewId) {
        viewModel.fetchInnerView(innerViewId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEventFlow.collectLatest { event ->
            when (event) {
                is InnerViewDetailUiEvent.TurnNotificationEvent -> {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    val toastText =
                        if (event.isOn && !hasPermission) {
                            context.getString(R.string.feature_home_toast_notification_permission)
                        } else if (event.isOn) {
                            context.getString(
                                R.string.feature_home_toast_notification_on,
                                uiState.title
                            )
                        } else {
                            context.getString(
                                R.string.feature_home_toast_notification_off,
                                uiState.title
                            )
                        }

                    onShowToast(toastText)
                }
            }
        }
    }

    InnerViewDetailContent(
        innerViewId = innerViewId,
        uiState = uiState,
        onBackClick = onBackClick,
        onClickInterviewGroup = { isRecording, id, title ->
            if (isRecording) navigateToRecord(innerViewId, id, title)
            else navigateToInterviewGroup()
        },
        onNotificationClick = { isOn -> viewModel.changeNotificationState(innerViewId, isOn) },
        navigateToInnerViewQuestion = navigateToInnerViewQuestion,
        addInterviewGroup = { viewModel.addInnerViewGroup(innerViewId) }
    )
}

@Composable
private fun InnerViewDetailContent(
    innerViewId: String,
    uiState: InnerViewDetailUiState,
    onBackClick: () -> Unit,
    onClickInterviewGroup: (Boolean, Int, String) -> Unit,
    onNotificationClick: (Boolean) -> Unit,
    navigateToInnerViewQuestion: (String) -> Unit,
    addInterviewGroup: () -> Unit
) {
    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = uiState.title,
            navigationType = TopAppBarNavigationType.Back,
            onNavigationClick = { onBackClick() },
            actionButtons = {
                InnerViewAppBarIcon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    navigationIconContentDescription = null,
                    onClick = { navigateToInnerViewQuestion(innerViewId) }
                )
                InnerViewAppBarIcon(
                    imageVector =
                    if (uiState.isNotificationOn) Icons.Filled.Notifications
                    else ImageVector.vectorResource(R.drawable.ic_notifications_off),
                    navigationIconContentDescription = null,
                    onClick = { onNotificationClick(!uiState.isNotificationOn) }
                )
                InnerViewAppBarIcon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    navigationIconContentDescription = null
                )
            }
        )
        Box(
            modifier = Modifier
                .systemBarsPadding()
                .padding(top = appBarSize)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            val lazyGridState = rememberLazyGridState()
            val coroutineScope = rememberCoroutineScope()
            val lastScrolledBackward by remember {
                derivedStateOf {
                    lazyGridState.lastScrolledBackward || !lazyGridState.canScrollBackward
                }
            }

            val animatedHeight by animateDpAsState(
                targetValue = if (lastScrolledBackward) 60.dp else 0.dp,
                label = "collapsing animation"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .zIndex(1f)
                    .background(MaterialTheme.colorScheme.background)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { coroutineScope.launch { lazyGridState.animateScrollToItem(0) } }
                    .height(animatedHeight)
            ) {
                val reactivateAt =
                    uiState.reactivateAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text =
                    if (uiState.isRecording) stringResource(R.string.feature_home_innerview_detail_description_recording)
                    else if (uiState.isActivated) stringResource(R.string.feature_home_innerview_detail_description)
                    else stringResource(
                        R.string.feature_home_innerview_detail_description_date,
                        reactivateAt
                    ),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            if (uiState.type == InnerViewType.DAY) {
                DailyInterviewGroupList(
                    title = uiState.title,
                    interviewGroups = uiState.interviewGroups,
                    onClickInterviewGroup = onClickInterviewGroup,
                    lazyGridState = lazyGridState
                )
            } else {
                InterviewGroupList(
                    title = uiState.title,
                    interviewGroups = uiState.interviewGroups,
                    onClickInterviewGroup = onClickInterviewGroup,
                    lazyGridState = lazyGridState
                )
            }
            //if(innerViewDetailUiState.isActivated) {
            InnerViewFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Paddings.large, bottom = Paddings.large),
                iconImageVector = Icons.Filled.Add,
                text = stringResource(R.string.feature_home_innerview_detail_start_interview),
                onClick = { addInterviewGroup() }
            )
            //}
        }
    }
}

@Composable
private fun DailyInterviewGroupList(
    title: String,
    interviewGroups: ImmutableList<InterviewGroup>,
    onClickInterviewGroup: (Boolean, Int, String) -> Unit,
    lazyGridState: LazyGridState
) {
    val interviews = interviewGroups.groupBy {
        it.createdAt.withZoneSameInstant(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy - MM월"))
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Paddings.large, vertical = Paddings.medium),
        state = lazyGridState,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(Paddings.medium),
        horizontalArrangement = Arrangement.spacedBy(Paddings.medium),
    ) {

        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(50.dp)) }

        interviews.keys.forEach { key ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = key,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            items(interviews[key]!!, key = { it.id }) {
                val createAt = it.createdAt.withZoneSameInstant(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

                InterviewGroupItem(
                    modifier = Modifier
                        .height(150.dp)
                        .clickable {
                            onClickInterviewGroup(
                                it.isRecording,
                                it.id,
                                "$title : $createAt"
                            )
                        },
                    interviewGroup = it,
                    dateTextStyle = MaterialTheme.typography.labelLarge,
                    createAt = createAt,
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun InterviewGroupList(
    title: String,
    interviewGroups: ImmutableList<InterviewGroup>,
    onClickInterviewGroup: (Boolean, Int, String) -> Unit,
    lazyGridState: LazyGridState
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Paddings.large),
        state = lazyGridState,
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(Paddings.medium),
        horizontalArrangement = Arrangement.spacedBy(Paddings.medium),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(50.dp)) }

        items(interviewGroups, key = { it.id }) {

            val createAt = it.createdAt.withZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

            InterviewGroupItem(
                modifier = Modifier
                    .height(240.dp)
                    .clickable {
                        onClickInterviewGroup(it.isRecording, it.id, "$title : $createAt")
                    },
                interviewGroup = it,
                dateTextStyle = MaterialTheme.typography.titleSmall,
                createAt = createAt,
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InnerViewDetailContentPreview() {
    InnerViewTheme {
        InnerViewDetailContent(
            innerViewId = "",
            uiState = InnerViewDetailUiState(
                title = "title",
                interviewGroups = persistentListOf(
                    InterviewGroup(
                        id = 1,
                        createdAt = ZonedDateTime.now(),
                        isRecording = true,
                        questionCount = 30,
                        thumbnailVideoPath = null
                    ),
                    InterviewGroup(
                        id = 2,
                        createdAt = ZonedDateTime.now(),
                        isRecording = true,
                        questionCount = 20,
                        thumbnailVideoPath = null
                    ),
                    InterviewGroup(
                        id = 3,
                        createdAt = ZonedDateTime.now(),
                        isRecording = true,
                        questionCount = 10,
                        thumbnailVideoPath = null
                    )
                )
            ),
            onBackClick = {},
            onClickInterviewGroup = { _, _, _ -> },
            navigateToInnerViewQuestion = {},
            onNotificationClick = {},
            addInterviewGroup = {}
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun DailyInnerViewDetailContentPreview() {
    InnerViewTheme {
        InnerViewDetailContent(
            innerViewId = "",
            uiState = InnerViewDetailUiState(
                title = "title",
                type = InnerViewType.DAY,
                interviewGroups = persistentListOf(
                    InterviewGroup(
                        id = 1,
                        createdAt = ZonedDateTime.now(),
                        isRecording = true,
                        questionCount = 30,
                        thumbnailVideoPath = null
                    ),
                    InterviewGroup(
                        id = 2,
                        createdAt = ZonedDateTime.now().minusMonths(1),
                        isRecording = true,
                        questionCount = 20,
                        thumbnailVideoPath = null
                    ),
                    InterviewGroup(
                        id = 3,
                        createdAt = ZonedDateTime.now().minusMonths(2),
                        isRecording = true,
                        questionCount = 10,
                        thumbnailVideoPath = null
                    )
                )
            ),
            onBackClick = {},
            onClickInterviewGroup = { _, _, _ -> },
            navigateToInnerViewQuestion = {},
            onNotificationClick = {},
            addInterviewGroup = {}
        )
    }
}
