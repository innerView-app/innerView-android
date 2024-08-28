package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InnerViewContent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInnerViewContentUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    operator fun invoke(innerViewId: String): Flow<InnerViewContent> =
        innerViewRepository.getInnerViewContent(innerViewId)
}