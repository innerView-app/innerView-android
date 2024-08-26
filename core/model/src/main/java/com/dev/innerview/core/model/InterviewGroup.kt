package com.dev.innerview.core.model

import java.time.ZonedDateTime

data class InterviewGroup(
    val id: String,
    val createdAt: ZonedDateTime,
    val recordState: RecordState,
    val questionCount: Int,
    val thumbnailVideoPath: String?
)