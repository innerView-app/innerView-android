package com.dev.innerview.feature.home.component

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.component.InnerViewCard
import com.dev.innerview.core.designsystem.component.InnerViewDialog
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.feature.home.R
import com.dev.innerview.feature.home.model.InnerViewItemUiState
import java.time.Duration
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InnerViewItem(
    innerViewItemState: InnerViewItemUiState,
    onInnerViewClick: (String) -> Unit,
    onInnerViewLongClick: (String) -> Unit,
    onSelectInnerViewDelete: (String) -> Unit,
    onInnerViewDeleteRequest: (String) -> Unit
) {

    val createdAt = innerViewItemState.createdAt.withZoneSameInstant(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

    val daysBetween =
        Duration.between(innerViewItemState.createdAt, ZonedDateTime.now(ZoneOffset.UTC)).toDays()

    InnerViewCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .combinedClickable(
                onClick = { onInnerViewClick(innerViewItemState.id) },
                onLongClick = { onInnerViewLongClick(innerViewItemState.id) }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Paddings.large)
        ) {
            Text(
                modifier = Modifier.align(Alignment.TopStart),
                text = innerViewItemState.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2
            )

            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                text = "$createdAt ~ D+${daysBetween}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = when (innerViewItemState.type) {
                    InnerViewType.YEAR -> stringResource(id = R.string.feature_home_innerview_type_year)
                    InnerViewType.MONTH -> stringResource(id = R.string.feature_home_innerview_type_month)
                    InnerViewType.WEEK -> stringResource(id = R.string.feature_home_innerview_type_week)
                    InnerViewType.DAY -> stringResource(id = R.string.feature_home_innerview_type_day)
                },
                style = MaterialTheme.typography.labelSmall
            )
            InnerViewDropdownMenu(
                modifier = Modifier,
                itemUiState = innerViewItemState,
                onDeleteInnerView = { onSelectInnerViewDelete(innerViewItemState.id) },
                onDismissRequest = { onInnerViewLongClick(innerViewItemState.id) }
            )
        }
    }

    if (innerViewItemState.isInnerViewDeleteDialogVisible) {
        InnerViewDialog(
            titleText = stringResource(
                R.string.feature_home_innerview_delete_dialog_title,
                innerViewItemState.title
            ),
            contentText = stringResource(
                R.string.feature_home_innerview_delete_dialog_content,
                innerViewItemState.title
            ),
            confirmText = stringResource(R.string.feature_home_innerview_delete),
            dismissText = stringResource(R.string.feature_home_dialog_dismiss),
            onDismissRequest = { onSelectInnerViewDelete(innerViewItemState.id) },
            onConfirmRequest = { onInnerViewDeleteRequest(innerViewItemState.id) }
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun InnerViewContentPreview() {
    InnerViewTheme {
        InnerViewItem(
            onInnerViewClick = {},
            onInnerViewLongClick = {},
            onSelectInnerViewDelete = {},
            onInnerViewDeleteRequest = {},
            innerViewItemState = InnerViewItemUiState(
                id = "",
                title = "innerView title",
                type = InnerViewType.YEAR,
                createdAt = ZonedDateTime.now(ZoneOffset.UTC),
                isInnerViewDeleteDialogVisible = true,
            )
        )
    }
}