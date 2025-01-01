package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InnerProjectComponents
import javax.inject.Inject

class UpdateInnerProjectUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(
        innerProjectId: Int,
        innerProjectComponents: InnerProjectComponents
    ) = innerViewRepository.updateInnerProject(innerProjectId, innerProjectComponents)
}