package com.dev.innerview.core.data.mapper

import com.dev.innerview.core.database.schema.InterviewSchema
import com.dev.innerview.core.model.Interview
import java.time.ZonedDateTime

internal fun InterviewSchema.toData(): Interview =
    Interview(
        createdAt = createdAt?.let { ZonedDateTime.parse(it) },
        question = question,
        isRequired = isRequired,
        isRecordComplete = innerProject != null,
        thumbnailVideoPath = innerProject?.videoPath
    )