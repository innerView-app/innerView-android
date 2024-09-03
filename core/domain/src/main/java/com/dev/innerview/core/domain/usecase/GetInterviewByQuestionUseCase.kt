package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.Interview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInterviewByQuestionUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    operator fun invoke(innerViewId: String, question: String): Flow<List<Interview>> =
        innerViewRepository.getInterviewByQuestion(innerViewId, question)
}