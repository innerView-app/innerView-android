package com.dev.innerview.feature.home.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.component.InterviewGroupCard
import com.dev.innerview.core.designsystem.component.OutlinedText
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InterviewGroup
import com.dev.innerview.feature.home.R

@Composable
fun InterviewGroupItem(
    modifier: Modifier = Modifier,
    interviewGroup: InterviewGroup,
    dateTextStyle: TextStyle,
    createAt: String
) {
    InterviewGroupCard(
        modifier = modifier,
        filePath = interviewGroup.thumbnailVideoPath
    ) {
        if (interviewGroup.isRecording) {
            OutlinedText(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(Paddings.medium),
                text = stringResource(R.string.feature_home_innerview_detail_interview_group_description_recording),
                style = MaterialTheme.typography.labelMedium
                    .copy(color = MaterialTheme.colorScheme.onError),
                outlineColor = MaterialTheme.colorScheme.error,
                outlineDrawStyle = Stroke(width = 4f)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = Paddings.medium, bottom = Paddings.medium)
        ) {
            OutlinedText(
                text = stringResource(
                    R.string.feature_home_innerview_detail_interview_group_description,
                    interviewGroup.questionCount
                ),
                style = MaterialTheme.typography.labelSmall
                    .copy(color = MaterialTheme.colorScheme.onTertiary),
                outlineColor = MaterialTheme.colorScheme.tertiary,
                outlineDrawStyle = Stroke(width = 4f)
            )
            Spacer(modifier = Modifier.size(5.dp))
            OutlinedText(
                text = createAt,
                style = dateTextStyle.copy(color = MaterialTheme.colorScheme.onTertiary),
                outlineColor = MaterialTheme.colorScheme.tertiary,
                outlineDrawStyle = Stroke(width = 5f)
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun InnerViewContentPreview() {
    InnerViewTheme {
        InterviewGroupItem(
            modifier = Modifier
                .height(240.dp)
                .width(180.dp),
            interviewGroup = InterviewGroup(),
            dateTextStyle = MaterialTheme.typography.titleSmall,
            createAt = "yyyy.mm.dd"
        )
    }
}