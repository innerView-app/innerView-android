package com.dev.innerview.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.InterviewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class AlarmHelper @Inject constructor(
    private val context: Context,
    private val repository: InnerViewRepository
) {
    private val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun registerNotificationAlarm(
        innerViewId: String,
        innerViewType: InnerViewType,
        innerViewTitle: String,
        lastInnerViewTime: ZonedDateTime
    ) {
        val lastTime = lastInnerViewTime.withZoneSameInstant(ZoneId.systemDefault())
        val notificationTime = findNotificationTime(lastTime, innerViewType)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = INTENT_ACTION_NOTIFICATION_ALARM
            putExtra(INTENT_EXTRA_ID, innerViewId)
            putExtra(INTENT_EXTRA_TITLE, innerViewTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            innerViewId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmMgr.setRepeating(
            AlarmManager.RTC,
            notificationTime,
            NOTIFICATION_INTERVAL,
            pendingIntent
        )
    }

    fun cancelAlarm(id: String) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            Intent(context, AlarmReceiver::class.java)
                .apply { this.action = INTENT_ACTION_NOTIFICATION_ALARM },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmMgr.cancel(pendingIntent)
    }

    internal fun registerNotificationAlarms() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getInnerViews().first()
                .filter { it.isNotificationOn }
                .map { repository.getInnerViewContent(it.id).first() }
                .forEach { content ->
                    registerNotificationAlarm(
                        innerViewId = content.innerView.id,
                        lastInnerViewTime = content.interviewGroups
                            .find { it.interviewState == InterviewState.COMPLETE }
                            ?.createdAt
                            ?: return@launch,
                        innerViewType = content.innerView.type,
                        innerViewTitle = content.innerView.title
                    )
                }
        }
    }

    private fun findNotificationTime(
        lastTime: ZonedDateTime,
        innerViewType: InnerViewType
    ): Long {
        val notificationTime = LocalTime.of(NOTIFICATION_TIME_HOUR, NOTIFICATION_TIME_MINUTE)
        val now = ZonedDateTime.now()

        val expectedTime = when (innerViewType) {
            InnerViewType.DAY -> lastTime.plusDays(1)
            InnerViewType.WEEK -> lastTime.plusWeeks(1)
            InnerViewType.MONTH -> lastTime.plusMonths(1)
            InnerViewType.YEAR -> lastTime.plusYears(1)
        }
            .with(notificationTime)


        val latestTime = now.with(notificationTime)
            .takeIf { it.isAfter(now) }
            ?: now.with(notificationTime).plusDays(1)

        return maxOf(expectedTime, latestTime).toInstant().toEpochMilli()
    }

    companion object {
        private const val NOTIFICATION_TIME_HOUR = 20
        private const val NOTIFICATION_TIME_MINUTE = 0
        private const val NOTIFICATION_INTERVAL = 24 * 60 * 60 * 1000L

        internal const val INTENT_ACTION_NOTIFICATION_ALARM = "notificationAlarm"

        internal const val INTENT_EXTRA_ID = "intentExtraId"
        internal const val INTENT_EXTRA_TITLE = "intentExtraTitle"
    }
}