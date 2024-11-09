package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InterviewGroupContent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInterviewGroupContentUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    operator fun invoke(innerViewId: String, interviewGroupId: Int): Flow<InterviewGroupContent> =
        innerViewRepository.getInterviewGroupContentById(innerViewId, interviewGroupId)
}