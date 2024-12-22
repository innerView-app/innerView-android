package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.QuestionRepository
import com.dev.innerview.core.model.InnerViewType
import javax.inject.Inject

class GetRecommendQuestionsByTypeUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(type: InnerViewType): List<String> =
        questionRepository.getRecommendQuestionsByType(type)
}