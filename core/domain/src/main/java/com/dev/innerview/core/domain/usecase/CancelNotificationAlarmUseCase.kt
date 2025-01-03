package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.notification.AlarmHelper
import javax.inject.Inject

class CancelNotificationAlarmUseCase @Inject constructor(
    private val alarmHelper: AlarmHelper
) {
    operator fun invoke(innerViewId: String) = alarmHelper.cancelAlarm(innerViewId)
}