package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InterviewGroup
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInterviewGroupUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    operator fun invoke(innerViewId: String): Flow<List<InterviewGroup>> =
        innerViewRepository.getInterviewGroups(innerViewId)
}