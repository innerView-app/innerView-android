package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import javax.inject.Inject

class DeleteInterviewGroupUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(innerViewId: String, interviewGroupId: String) =
        innerViewRepository.deleteInterviewGroup(innerViewId, interviewGroupId)
}