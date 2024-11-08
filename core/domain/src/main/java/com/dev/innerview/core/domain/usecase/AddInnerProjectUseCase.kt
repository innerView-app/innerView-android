package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import javax.inject.Inject

class AddInnerProjectUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(
        title: String,
        innerViewId: String? = null,
        interviewGroupId: Int? = null,
        jsonData: String? = null
    ): Int = innerViewRepository.addInnerProject(title, innerViewId, interviewGroupId, jsonData)
}