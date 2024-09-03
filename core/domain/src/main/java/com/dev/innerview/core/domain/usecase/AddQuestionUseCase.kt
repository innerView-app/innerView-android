package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import javax.inject.Inject

class AddQuestionUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    ) = innerViewRepository.addQuestion(innerViewId, interviewGroupId, question)
}