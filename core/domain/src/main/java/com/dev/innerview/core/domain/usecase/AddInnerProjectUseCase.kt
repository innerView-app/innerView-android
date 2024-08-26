package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import javax.inject.Inject

class AddInnerProjectUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(
        innerViewId: String,
        interviewGroupId: String,
        question: String
    ) = innerViewRepository.addInnerProject(innerViewId, interviewGroupId, question)
}