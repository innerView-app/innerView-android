package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import javax.inject.Inject

class ReorderQuestionsUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(
        innerViewId: String,
        oldIndex: Int,
        newIndex: Int
    ) = innerViewRepository.reorderQuestions(innerViewId, oldIndex, newIndex)
}