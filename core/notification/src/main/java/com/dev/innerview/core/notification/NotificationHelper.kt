package com.dev.innerview.core.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dev.innerview.core.model.RenderProgress
import com.dev.innerview.core.navigation.getDeepLinkOf
import javax.inject.Inject

class NotificationHelper @Inject constructor(private val context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    internal fun createNotification(
        notificationId: String,
        notificationTitle: String
    ) {
        val notificationIntent = Intent(
            Intent.ACTION_VIEW,
            getDeepLinkOf("detail/$notificationId")
        )

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notificationTitle)
            .setContentText(context.getString(R.string.core_notification_content))
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

    fun createRenderNotification(
        innerProjectTitle: String,
        innerProjectId: Int,
        progress: Int,
        stopPendingIntent: PendingIntent
    ): Notification {
        val notificationTitle =
            context.getString(R.string.core_notification_render_title, innerProjectTitle)
        val notificationText = if (progress >= 100) {
            context.getString(R.string.core_notification_render_gallery_saving)
        } else {
            context.getString(R.string.core_notification_render_progress, progress)
        }

        val notificationIntent = Intent(
            Intent.ACTION_VIEW,
            getDeepLinkOf("edit/$innerProjectId")
        )

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, false)
            .let {
                if (progress < 100) {
                    it.addAction(
                        R.drawable.ic_cancel_24,
                        context.getString(R.string.core_notification_render_cancel_action),
                        stopPendingIntent
                    )
                } else {
                    it
                }
            }
            .setContentIntent(pendingIntent)
            .build()
    }

    fun notifyRenderCompleteNotification(
        innerProjectTitle: String,
        innerProjectId: Int,
        renderProgress: RenderProgress
    ) {
        val notificationTitle =
            context.getString(R.string.core_notification_render_complete_title, innerProjectTitle)
        val notificationText = when (renderProgress) {
            is RenderProgress.Completed -> {
                context.getString(R.string.core_notification_render_complete)
            }

            is RenderProgress.Error -> {
                context.getString(R.string.core_notification_render_error)
            }

            is RenderProgress.Cancelled -> {
                context.getString(R.string.core_notification_render_cancel)
            }

            else -> ""
        }

        val notificationIntent = Intent(
            Intent.ACTION_VIEW,
            getDeepLinkOf("edit/$innerProjectId")
        )

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(innerProjectId, notification)
            } catch (_: SecurityException) {

            }
        }
    }

    private fun createNotificationChannel() {
        val id = CHANNEL_ID
        val name = context.getString(R.string.core_notification_channel_name)
        val description = context.getString(R.string.core_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
            .apply { this.description = description }

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "InnerViewNotificationChannel"
    }
}