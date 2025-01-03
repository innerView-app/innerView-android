package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.data_api.InnerViewRepository
import javax.inject.Inject

class ChangeInnerViewNotificationUseCase @Inject constructor(
    private val innerViewRepository: InnerViewRepository
) {
    suspend operator fun invoke(innerViewId: String, isOn: Boolean) =
        innerViewRepository.changeInnerViewNotification(innerViewId, isOn)
}