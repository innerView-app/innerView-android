package com.dev.innerview.core.model

import java.time.ZonedDateTime

data class Interview(
    val createdAt: ZonedDateTime,
    val question: String,
    var isRequired: Boolean,
    val recordState: RecordState,
    val thumbnailVideoPath: String?
)