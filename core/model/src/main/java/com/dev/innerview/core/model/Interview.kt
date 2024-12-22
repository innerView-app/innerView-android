package com.dev.innerview.core.model

import java.time.ZonedDateTime

data class Interview(
    val createdAt: ZonedDateTime? = null,
    val question: String = "",
    var isRequired: Boolean = false,
    val isRecordComplete: Boolean = false,
    val innerProjectId: Int = 0,
    val thumbnailVideoPath: String? = null
)