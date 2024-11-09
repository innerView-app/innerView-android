package com.dev.innerview.feature.record.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.component.InterviewCard
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.Interview
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PrevInterviewItem(
    interview: Interview,
    onClick: () -> Unit,
) {
    InterviewCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        interviewDescription = interview.question,
        filePath = interview.thumbnailVideoPath
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(Paddings.large),
            text = interview.createdAt?.withZoneSameInstant(ZoneId.systemDefault())
                ?.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) ?: "",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PrevInterviewItemPreview() {
    InnerViewTheme {
        PrevInterviewItem(
            interview = Interview(
                createdAt = ZonedDateTime.now(),
                question = ""
            ),
            onClick = {}
        )
    }
}