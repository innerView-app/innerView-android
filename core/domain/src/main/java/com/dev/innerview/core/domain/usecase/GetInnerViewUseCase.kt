package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InnerView
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInnerViewUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    operator fun invoke(): Flow<List<InnerView>> = innerViewRepository.getInnerViews()
}