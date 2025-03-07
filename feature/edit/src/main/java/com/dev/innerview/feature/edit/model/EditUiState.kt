package com.dev.innerview.feature.edit.model

import androidx.compose.runtime.Immutable
import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.Interview
import com.dev.innerview.core.model.InterviewGroup
import com.dev.innerview.core.model.InterviewPiece
import com.dev.innerview.core.model.Subtitle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.ZonedDateTime

@Immutable
data class EditUiState(
    val innerProjectId: Int = 0,
    val title: String = "",
    val isPlaying: Boolean = false,
    val currentMediaItemIndex: Int = 0,
    val position: Long = 0,
    val duration: Long = 0,
    val aspectRatio: Float = 9F / 16F,
    val zoom: Float = 0.1f,
    val media: ImmutableList<MediaUiState> = persistentListOf(),
    val accumulatedDurations: ImmutableList<Long> = persistentListOf(),
    val subtitles: ImmutableList<Subtitle> = persistentListOf(),
)

@Immutable
data class MediaUiState(
    val medium: InterviewPiece = InterviewPiece(),
    val selected: Boolean = false,
)

@Immutable
data class MediaAddUiState(
    val isOpen: Boolean = false,
    val selectedInnerProjectId: ImmutableList<Int> = persistentListOf(),
    val innerViews: ImmutableList<InnerViewSelectUiState> = persistentListOf()
)

@Immutable
data class InnerViewSelectUiState(
    val innerViewId: String = "",
    val title: String = "",
    val innerViewContents: ImmutableList<InterviewGroupSelectUiState> = persistentListOf(),
    val isOpen: Boolean = false
) {
    companion object {
        fun create(innerView: InnerView): InnerViewSelectUiState {
            return InnerViewSelectUiState(
                innerViewId = innerView.id,
                title = innerView.title,
                innerViewContents = persistentListOf(),
                isOpen = false,
            )
        }
    }
}

@Immutable
data class InterviewGroupSelectUiState(
    val innerViewId: String = "",
    val interviewGroupId: Int = 0,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val interviewContents: ImmutableList<InterviewSelectUiState> = persistentListOf(),
    val isOpen: Boolean = false
) {
    companion object {
        fun create(
            innerViewId: String,
            interviewGroup: InterviewGroup
        ): InterviewGroupSelectUiState {
            return InterviewGroupSelectUiState(
                innerViewId = innerViewId,
                interviewGroupId = interviewGroup.id,
                createdAt = interviewGroup.createdAt,
                interviewContents = persistentListOf(),
                isOpen = false,
            )
        }
    }
}

@Immutable
data class InterviewSelectUiState(
    val innerViewId: String = "",
    val interviewGroupId: Int = 0,
    val innerProjectId: Int = 0,
    val thumbnailVideoPath: String? = null,
    val question: String = "",
    val selected: Boolean = false
) {
    companion object {
        fun create(
            innerViewId: String,
            interviewGroupId: Int,
            interview: Interview
        ): InterviewSelectUiState {
            return InterviewSelectUiState(
                innerViewId = innerViewId,
                interviewGroupId = interviewGroupId,
                innerProjectId = interview.innerProjectId,
                thumbnailVideoPath = interview.thumbnailVideoPath,
                question = interview.question,
                selected = false,
            )
        }
    }
}