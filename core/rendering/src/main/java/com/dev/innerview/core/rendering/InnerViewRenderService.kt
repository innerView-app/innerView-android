package com.dev.innerview.core.rendering

import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.MediaStore
import android.widget.Toast
import com.dev.innerview.core.domain.usecase.GetInnerProjectByIdUseCase
import com.dev.innerview.core.model.InnerProject
import com.dev.innerview.core.model.RenderProgress
import com.dev.innerview.core.model.Scale
import com.dev.innerview.core.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class InnerViewRenderService : Service() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private var renderJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Inject
    lateinit var getInnerProjectByIdUseCase: GetInnerProjectByIdUseCase

    @Inject
    lateinit var innerViewRender: InnerViewRender

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val innerProjectId = intent?.getIntExtra(INTENT_EXTRA_INNER_PROJECT_ID, -1)
        val scale = Scale.stringToScale(intent?.getStringExtra(INTENT_EXTRA_SCALE))

        when (action) {
            INTENT_ACTION_START_RENDER -> {
                if (innerProjectId != null && innerProjectId != -1) {
                    renderVideo(innerProjectId, scale)
                }
            }

            INTENT_ACTION_STOP_RENDER -> {
                if (innerProjectId != null && innerProjectId != -1) {
                    innerViewRender.cancelRendering()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun renderVideo(innerProjectId: Int, scale: Scale) {
        if (renderJob != null) {
            Toast.makeText(
                this@InnerViewRenderService,
                getString(R.string.core_rendering_already_running_text),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val stopPendingIntent = createStopPendingIntent(innerProjectId)

        renderJob = serviceScope.launch {
            var innerProject = InnerProject()
            var renderFilePath = ""
            try {
                innerProject = getInnerProjectByIdUseCase(innerProjectId).first()
                renderFilePath = "${innerProject.title}_${System.currentTimeMillis()}.mp4"

                startForeground(
                    FOREGROUND_ID, notificationHelper.createRenderNotification(
                        innerProject.title,
                        innerProjectId,
                        0,
                        stopPendingIntent
                    )
                )

                innerViewRender.startRendering(
                    innerProject.innerProjectComponents,
                    filesDir.absolutePath,
                    renderFilePath,
                    scale
                )
                innerViewRender.progressFlow.collectLatest { progress ->
                    when (progress) {
                        is RenderProgress.InProgress -> {
                            startForeground(
                                FOREGROUND_ID, notificationHelper.createRenderNotification(
                                    innerProject.title,
                                    innerProjectId,
                                    progress.percentage,
                                    stopPendingIntent
                                )
                            )
                        }

                        is RenderProgress.Completed -> {
                            startForeground(
                                FOREGROUND_ID, notificationHelper.createRenderNotification(
                                    innerProject.title,
                                    innerProjectId,
                                    100,
                                    stopPendingIntent
                                )
                            )

                            copyToMediaStore(renderFilePath)

                            notificationHelper.notifyRenderCompleteNotification(
                                innerProject.title,
                                innerProjectId,
                                progress
                            )
                            this.cancel()
                        }

                        is RenderProgress.Cancelled -> {
                            notificationHelper.notifyRenderCompleteNotification(
                                innerProject.title,
                                innerProjectId,
                                progress
                            )
                            this.cancel()
                        }

                        is RenderProgress.Error -> {
                            notificationHelper.notifyRenderCompleteNotification(
                                innerProject.title,
                                innerProjectId,
                                progress
                            )
                            this.cancel()
                        }
                    }
                }
            } finally {
                File(filesDir, renderFilePath).delete()
                innerViewRender.cancelRendering()
                stopSelf()
            }
        }
    }

    private fun copyToMediaStore(renderFilePath: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, renderFilePath)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(
                MediaStore.Video.Media.RELATIVE_PATH,
                "Movies/innerView"
            )
        }

        val uri: Uri? =
            this.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

        uri?.let {
            this.contentResolver.openOutputStream(it)?.use { outputStream ->
                File(filesDir, renderFilePath).inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    private fun createStopPendingIntent(innerProjectId: Int): PendingIntent {
        val stopIntent = Intent(this, InnerViewRenderService::class.java).apply {
            action = INTENT_ACTION_STOP_RENDER
            putExtra(INTENT_EXTRA_INNER_PROJECT_ID, innerProjectId)
        }
        return PendingIntent.getService(
            this,
            innerProjectId,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        renderJob?.let { job ->
            if (job.isActive) {
                job.cancel()
            }
        }
        serviceScope.cancel()
    }

    companion object {
        const val FOREGROUND_ID = -10

        const val INTENT_ACTION_START_RENDER = "initInnerViewStartRender"
        const val INTENT_ACTION_STOP_RENDER = "stopInnerViewStopRender"

        const val INTENT_EXTRA_INNER_PROJECT_ID = "intentExtraInnerProjectId"
        const val INTENT_EXTRA_SCALE = "intentExtraScale"
    }
}