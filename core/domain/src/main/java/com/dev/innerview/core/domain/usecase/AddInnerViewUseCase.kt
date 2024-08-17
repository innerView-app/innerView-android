package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InnerViewType
import javax.inject.Inject

class AddInnerViewUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(title: String, type: InnerViewType) =
        innerViewRepository.addInnerView(title, type)
}