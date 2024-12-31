package com.dev.innerview.core.model

import java.time.ZonedDateTime

data class InterviewGroup(
    val id: Int = 0,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val recordState: RecordState = RecordState.RECORDING,
    val questionCount: Int = 0,
    val thumbnailVideoPath: String? = null
)