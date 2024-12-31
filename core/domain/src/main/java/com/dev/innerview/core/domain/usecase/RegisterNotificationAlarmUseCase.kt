package com.dev.innerview.core.domain.usecase

import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.notification.AlarmHelper
import java.time.ZonedDateTime
import javax.inject.Inject

class RegisterNotificationAlarmUseCase @Inject constructor(
    private val alarmHelper: AlarmHelper
) {
    operator fun invoke(
        innerViewId: String,
        innerViewType: InnerViewType,
        innerViewTitle: String,
        lastInnerViewTime: ZonedDateTime
    ) = alarmHelper.registerNotificationAlarm(
        innerViewId,
        innerViewType,
        innerViewTitle,
        lastInnerViewTime
    )
}