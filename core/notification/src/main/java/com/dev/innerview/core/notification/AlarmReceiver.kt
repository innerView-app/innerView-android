package com.dev.innerview.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmHelper: AlarmHelper

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> alarmHelper.registerNotificationAlarms()

            AlarmHelper.INTENT_ACTION_NOTIFICATION_ALARM -> {
                val id = intent.getStringExtra(AlarmHelper.INTENT_EXTRA_ID) ?: return
                val title = intent.getStringExtra(AlarmHelper.INTENT_EXTRA_TITLE) ?: return

                notificationHelper.createNotification(id, title)
            }
        }
    }
}