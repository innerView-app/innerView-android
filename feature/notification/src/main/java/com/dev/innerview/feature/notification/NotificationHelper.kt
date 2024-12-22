package com.dev.innerview.feature.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dev.innerview.core.navigation.getDeepLinkOf
import javax.inject.Inject

class NotificationHelper @Inject constructor(private val context: Context) {
    init { createNotificationChannel() }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    internal fun createNotification(
        notificationId: String,
        notificationTitle: String
    ) {
        val notificationIntent = Intent(
            Intent.ACTION_VIEW,
            getDeepLinkOf(notificationId)
        )

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(notificationTitle)
            .setContentText(context.getString(R.string.feature_notification_content))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId.hashCode(), notificationBuilder.build())
            } catch (_: SecurityException) {

            }
        }
    }

    private fun createNotificationChannel() {
        val id = CHANNEL_ID
        val name = context.getString(R.string.feature_notification_channel_name)
        val description = context.getString(R.string.feature_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
            .apply { this.description = description }

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "InnerviewNotificationChannel"
    }
}