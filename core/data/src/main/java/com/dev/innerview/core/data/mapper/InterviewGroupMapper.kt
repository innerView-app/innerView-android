package com.dev.innerview.core.data.mapper

import com.dev.innerview.core.database.schema.InterviewGroupSchema
import com.dev.innerview.core.model.InnerProjectComponents
import com.dev.innerview.core.model.InterviewGroup
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

internal fun InterviewGroupSchema.toData(): InterviewGroup {

    val thumbnailVideoPath = interviews.firstOrNull()?.innerProject?.let {
        val innerProjectComponents: InnerProjectComponents = Json.decodeFromString(it.jsonData)
        innerProjectComponents.media.firstOrNull()?.filePath ?: ""
    } ?: ""

    return InterviewGroup(
        id = id,
        createdAt = ZonedDateTime.parse(createdAt),
        isRecording = isRecording,
        questionCount = interviews.size,
        thumbnailVideoPath = thumbnailVideoPath
    )
}