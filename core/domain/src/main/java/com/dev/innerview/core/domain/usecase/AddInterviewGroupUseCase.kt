package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import javax.inject.Inject

class AddInterviewGroupUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(innerViewId: String, addPrevQuestions: Boolean = true) =
        innerViewRepository.addInterviewGroup(innerViewId, addPrevQuestions)
}