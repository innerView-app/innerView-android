package com.dev.innerview.feature.record.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.Interview
import com.dev.innerview.feature.record.R
import com.dev.innerview.feature.record.model.FilmingUiState
import kotlinx.collections.immutable.persistentListOf
import java.time.ZonedDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrevInterviewBottomSheet(
    filmingUiState: FilmingUiState,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onItemClick: () -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier,
        sheetState = sheetState,
        onDismissRequest = { onDismissRequest() },
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = null
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = Paddings.extra, vertical = Paddings.xextra),
            verticalArrangement = Arrangement.spacedBy(Paddings.large)
        ) {
            item {
                Text(
                    text = filmingUiState.question,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.size(20.dp))
            }

            items(
                filmingUiState.interviews.filter { it.thumbnailVideoPath != null },
                key = { it.thumbnailVideoPath!! }
            ) {
                PrevInterviewItem(
                    interview = it,
                    onClick = { onItemClick() }
                )
            }

            item {
                if (filmingUiState.interviews.none { it.thumbnailVideoPath != null }) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.feature_record_empty_prev_interview),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PrevInterviewBottomSheetPreview() {
    InnerViewTheme {

        val sheetState = rememberStandardBottomSheetState()

        PrevInterviewBottomSheet(
            filmingUiState = FilmingUiState(
                interviews = persistentListOf(
                    Interview(createdAt = ZonedDateTime.now(), thumbnailVideoPath = "1"),
                    Interview(createdAt = ZonedDateTime.now(), thumbnailVideoPath = "2"),
                    Interview(createdAt = ZonedDateTime.now(), thumbnailVideoPath = "3"),
                )
            ),
            sheetState = sheetState,
            onDismissRequest = {},
            onItemClick = {}
        )
    }
}