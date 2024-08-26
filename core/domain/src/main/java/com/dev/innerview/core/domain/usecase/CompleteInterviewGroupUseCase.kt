package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import javax.inject.Inject

class CompleteInterviewGroupUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(
        innerViewId: String, interviewGroupId: String
    ) = innerViewRepository.completeInterviewGroup(innerViewId, interviewGroupId)
}