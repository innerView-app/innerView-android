package com.dev.innerview.core.data.mapper

import com.dev.innerview.core.database.schema.InterviewSchema
import com.dev.innerview.core.model.InnerProjectComponents
import com.dev.innerview.core.model.Interview
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

internal fun InterviewSchema.toData(): Interview {

    val thumbnailVideoPath = innerProject?.let {
        val innerProjectComponents: InnerProjectComponents = Json.decodeFromString(it.jsonData)
        innerProjectComponents.videos.firstOrNull()?.filePath ?: ""
    } ?: ""

    return Interview(
        createdAt = createdAt?.let { ZonedDateTime.parse(it) },
        question = question,
        isRequired = isRequired,
        isRecordComplete = innerProject != null,
        innerProjectId = innerProject?._id ?: 0,
        thumbnailVideoPath = thumbnailVideoPath
    )
}