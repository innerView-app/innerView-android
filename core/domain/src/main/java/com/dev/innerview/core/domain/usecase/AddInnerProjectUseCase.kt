package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InnerProjectComponents
import javax.inject.Inject

class AddInnerProjectUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(
        title: String,
        innerViewId: String? = null,
        interviewGroupId: Int? = null,
        innerProjectComponents: InnerProjectComponents = InnerProjectComponents()
    ): Int = innerViewRepository.addInnerProject(title, innerViewId, interviewGroupId, innerProjectComponents)
}