package com.dev.innerview.core.model

import java.time.ZonedDateTime

data class InterviewGroup(
    val id: Int = 0,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val isRecording: Boolean = false,
    val questionCount: Int = 0,
    val thumbnailVideoPath: String? = null
)