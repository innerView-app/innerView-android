package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InnerProject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInnerProjectByIdUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    operator fun invoke(id: Int): Flow<InnerProject> = innerViewRepository.getInnerProjectById(id)
}