package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InnerViewContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.time.ZonedDateTime
import javax.inject.Inject

class GetInnerViewContentUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    operator fun invoke(innerViewId: String): Flow<InnerViewContent> {
        return innerViewRepository
            .getInnerViewContent(innerViewId)
            .onEach { content ->
                content.interviewGroups
                    .filter {
                        it.isRecording && it.createdAt.plusDays(10).isBefore(ZonedDateTime.now())
                    }
                    .forEach {
                        innerViewRepository.deleteInterviewGroup(
                            content.innerView.id,
                            it.id
                        )
                    }
            }
    }
}