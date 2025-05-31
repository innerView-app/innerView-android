package com.dev.innerview.core.rendering

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.dev.innerview.core.model.InnerProjectComponents
import com.dev.innerview.core.model.RenderProgress
import com.dev.innerview.core.model.Scale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class InnerViewRender @Inject constructor() {

    private val _progressFlow = MutableStateFlow<RenderProgress>(RenderProgress.InProgress(0))
    val progressFlow: StateFlow<RenderProgress> = _progressFlow.asStateFlow()

    private var currentSession: FFmpegSession? = null

    fun startRendering(
        projectComponents: InnerProjectComponents,
        fileDir: String,
        outputFilePath: String,
        scale: Scale,
    ) {
        _progressFlow.value = RenderProgress.InProgress(0)

        val totalDuration = projectComponents.media.sumOf {
            it.endPosition - it.startPosition
        }
        if (totalDuration <= 0) {
            _progressFlow.value = RenderProgress.Error
            return
        }

        _progressFlow.value = RenderProgress.InProgress(0)

        val ffmpegCommand = buildFFmpegCommand(projectComponents, fileDir, outputFilePath, scale)

        currentSession = FFmpegKit.executeAsync(
            ffmpegCommand,
            { session ->
                val returnCode = session.returnCode

                if (ReturnCode.isSuccess(returnCode)) {
                    _progressFlow.value = RenderProgress.Completed
                    println("FFmpeg completed")
                } else if (ReturnCode.isCancel(returnCode)) {
                    _progressFlow.value = RenderProgress.Cancelled
                    println("FFmpeg cancelled")
                } else {
                    _progressFlow.value = RenderProgress.Error
                    session.logs.forEach { log ->
                        println("FFmpeg log: ${log.level}-${log.message}")
                    }
                    println("FFmpeg failed")
                }
                currentSession = null
            },
            { log ->
                println("FFmpeg log: ${log.message}")
            },
            { statistics ->
                val currentTime = statistics.time
                val progressPercentage = ((currentTime / totalDuration.toDouble()) * 100).toInt()
                _progressFlow.value = RenderProgress.InProgress(progressPercentage.coerceIn(0, 100))
                println("FFmpeg statistics: $statistics")
            }
        )
    }

    fun cancelRendering() {
        currentSession?.let {
            FFmpegKit.cancel(it.sessionId)
        }
        currentSession = null
    }

    private fun buildFFmpegCommand(
        projectComponents: InnerProjectComponents,
        fileDir: String,
        outputFilePath: String,
        scale: Scale,
    ): String {

        val command = mutableListOf<String>()

        // 입력 파일 지정 (-i)
        projectComponents.media.forEach { interviewPiece ->
            command.add("-i \"$fileDir/${interviewPiece.filePath}\"")
        }

        // 필터 그래프 구성 (-filter_complex)
        // Todo 자막은 이 부분에서 추가
        val filterComplex = mutableListOf<String>()
        val trimOutputs = mutableListOf<String>()
        projectComponents.media.forEachIndexed { i, interviewPiece ->

            val startSeconds = interviewPiece.startPosition / 1000.0
            val endSeconds = interviewPiece.endPosition / 1000.0

            val videoOutput = "[v_out_$i]"
            val audioOutput = "[a_out_$i]"
            trimOutputs.add(videoOutput)
            trimOutputs.add(audioOutput)

            val videoStream =
                "[$i:v]trim=start=$startSeconds:end=$endSeconds,setpts=PTS-STARTPTS$videoOutput"
            val audioStream =
                "[$i:a]atrim=start=$startSeconds:end=$endSeconds,asetpts=PTS-STARTPTS$audioOutput"

            filterComplex.add(videoStream)
            filterComplex.add(audioStream)
        }
        val concat =
            trimOutputs.joinToString("") + "concat=n=${projectComponents.media.size}:v=1:a=1[v_concat_out][outa]"
        filterComplex.add(concat)
        val s = when (scale) {
            Scale.SIZE_1080P -> "1080:1920"
            Scale.SIZE_720P -> "720:1280"
            Scale.SIZE_480P -> "480:854"
        }
        val scaleCommend = "[v_concat_out]scale=$s[outv]"
        filterComplex.add(scaleCommend)
        command.add("-filter_complex \"${filterComplex.joinToString(";")}\"")

        // 스트림 매핑 (-map)
        command.add("-map \"[outv]\"")
        command.add("-map \"[outa]\"")

        // 출력 옵션
//        command.add("-c:v libx264") // x264는 FFmpegKit GPL 라이선스에 포함되어 있음.
        command.add("-c:v vp9")
        command.add("-c:a aac")

        command.add("-y \"$fileDir/$outputFilePath\"")

        return command.joinToString(" ")
    }
}