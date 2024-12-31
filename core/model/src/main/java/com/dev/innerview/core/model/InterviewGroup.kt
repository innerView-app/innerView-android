package com.dev.innerview.core.model

import java.time.ZonedDateTime

data class InterviewGroup(
    val id: Int = 0,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val interviewState: InterviewState = InterviewState.RECORDING,
    val questionCount: Int = 0,
    val thumbnailVideoPath: String? = null
)