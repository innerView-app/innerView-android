package com.dev.innerview.feature.record.camerax

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.camera.core.CameraSelector
import androidx.camera.core.TorchState
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.RecordState
import com.dev.innerview.feature.record.R
import com.dev.innerview.feature.record.component.CameraOptionMultiButton
import com.dev.innerview.feature.record.component.CameraOptionSingleButton
import com.dev.innerview.feature.record.component.RecordButton
import com.dev.innerview.feature.record.component.ScrimText
import com.dev.innerview.feature.record.model.RecordingState
import java.io.File
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
internal fun CameraScreen(
    recordingState: RecordingState,
    outputFileName: String,
    modifier: Modifier = Modifier,
    startRecording: () -> Unit,
    resetRecording: () -> Unit,
    pauseRecording: () -> Unit,
    resumeRecording: () -> Unit,
    updateFlashState: (Boolean) -> Unit,
    updateRecordingState: (Long, Long) -> Unit,
    addInnerProject: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val inInspectionMode = LocalInspectionMode.current

    val controller = remember {
        if (!inInspectionMode) {
            LifecycleCameraController(context).apply {
                setEnabledUseCases(CameraController.VIDEO_CAPTURE)
            }
        } else {
            null
        }
    }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var userCancelled by remember { mutableStateOf(false) }

    val tempFile by remember {
        mutableStateOf(File(context.filesDir, "interviews/temp.mp4"))
    }

    Box(
        modifier = modifier
    ) {
        AndroidView(
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller?.bindToLifecycle(lifecycleOwner)
                    controller?.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        ScrimText(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = Paddings.xlarge),
            text = formatDuration(recordingState.recordedDurationNanos)
        )

        ScrimText(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Paddings.xlarge, end = Paddings.large),
            text = formatBytesToMB(recordingState.numBytesRecorded)
        )

        RecordButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Paddings.extra),
            recordState = recordingState.recordState,
            onRecordStart = {
                recording = controller?.startRecording(
                    FileOutputOptions.Builder(tempFile).build(),
                    AudioConfig.create(true),
                    ContextCompat.getMainExecutor(context)
                ) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            startRecording()
                        }

                        is VideoRecordEvent.Status -> {
                            updateRecordingState(
                                event.recordingStats.recordedDurationNanos,
                                event.recordingStats.numBytesRecorded
                            )
                        }

                        is VideoRecordEvent.Finalize -> {
                            recording = null
                            resetRecording()
                            if (event.hasError()) {
                                println("비디오 저장 실패: ${event.error}")
                            } else if (userCancelled) {
                                tempFile.delete()
                                userCancelled = false
                                println("비디오 저장 취소")
                            } else {
                                val outputFile =
                                    File(context.filesDir, "interviews/$outputFileName")
                                tempFile.renameTo(outputFile)
                                println("비디오 저장 완료: ${outputFile.absolutePath}")
                                addInnerProject()
                            }
                        }
                    }
                }
            },
            onRecordPause = {
                recording?.pause()
                pauseRecording()
            },
            onRecordResume = {
                recording?.resume()
                resumeRecording()
            }
        )

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = Paddings.xlarge, bottom = Paddings.extra),
            visible = recordingState.recordState == RecordState.RECODING || recordingState.recordState == RecordState.PAUSE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CameraOptionSingleButton(
                imageVector = Icons.Filled.Clear,
                iconDescription = stringResource(R.string.feature_record_reset_icon_description),
                onClick = {
                    userCancelled = true
                    recording?.stop()
                }
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Paddings.xlarge, bottom = Paddings.extra),
            visible = recordingState.recordState == RecordState.PAUSE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CameraOptionSingleButton(
                imageVector = Icons.Filled.Check,
                iconDescription = stringResource(R.string.feature_record_complete_icon_description),
                onClick = {
                    recording?.stop()
                }
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = Paddings.xlarge),
            visible = recordingState.recordState != RecordState.RECODING,
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            CameraOptionMultiButton {
                CameraOptionSingleButton(
                    imageVector = Icons.Filled.Cameraswitch,
                    iconDescription = stringResource(R.string.feature_record_camera_switch_icon_description),
                    isScrim = false,
                    onClick = {
                        controller?.changeSelector()
                    }
                )

                val flashIcon = if (recordingState.isFlashOn) {
                    Icons.Filled.FlashOn
                } else {
                    Icons.Filled.FlashOff
                }
                val flashIconDescription = if (recordingState.isFlashOn) {
                    stringResource(R.string.feature_record_flash_on_icon_description)
                } else {
                    stringResource(R.string.feature_record_flash_off_icon_description)
                }

                CameraOptionSingleButton(
                    imageVector = flashIcon,
                    iconDescription = flashIconDescription,
                    isScrim = false,
                    onClick = {
                        controller?.changeTorchState()
                        updateFlashState(controller?.cameraInfo?.torchState?.value == TorchState.ON)
                    }
                )
            }
        }
    }
}

private fun formatDuration(recordedDurationNanos: Long): String {
    val totalSeconds = recordedDurationNanos / 1000000000L
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

private fun formatBytesToMB(numBytesRecorded: Long): String {
    val megabytes = numBytesRecorded / (1024.0 * 1024.0)
    return String.format(Locale.getDefault(), "%.1f MB", megabytes)
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun CameraScreenPreview() {
    InnerViewTheme {
        CameraScreen(
            recordingState = RecordingState(
                recordState = RecordState.IDLE,
                recordedDurationNanos = 0L,
                numBytesRecorded = 0L,
            ),
            outputFileName = "sample.mp4",
            startRecording = {},
            resetRecording = {},
            pauseRecording = {},
            resumeRecording = {},
            updateFlashState = {},
            updateRecordingState = { _, _ -> },
            addInnerProject = {}
        )
    }
}