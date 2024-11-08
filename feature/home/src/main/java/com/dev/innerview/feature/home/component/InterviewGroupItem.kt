package com.dev.innerview.feature.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.dev.innerview.core.designsystem.component.InterviewGroupCard
import com.dev.innerview.core.designsystem.component.OutlinedText
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InterviewGroup
import com.dev.innerview.feature.home.R

@Composable
fun InterviewGroupItem(
    modifier: Modifier,
    interviewGroup: InterviewGroup,
    dateTextStyle: TextStyle,
    createAt: String
) {
    InterviewGroupCard(
        modifier = modifier,
        filePath = interviewGroup.thumbnailVideoPath
    ) {
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
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                outlineColor = MaterialTheme.colorScheme.tertiary,
                outlineDrawStyle = Stroke(width = 4f)
            )
            Spacer(modifier = Modifier.size(5.dp))
            OutlinedText(
                text = createAt,
                style = dateTextStyle.copy(
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