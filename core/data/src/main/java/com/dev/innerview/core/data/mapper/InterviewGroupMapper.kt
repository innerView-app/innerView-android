package com.dev.innerview.core.data.mapper

import com.dev.innerview.core.database.schema.InterviewGroupSchema
import com.dev.innerview.core.model.InnerProjectComponents
import com.dev.innerview.core.model.InterviewGroup
import com.dev.innerview.core.model.RecordState
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

internal fun InterviewGroupSchema.toData(): InterviewGroup {

    val thumbnailVideoPath = interviews.firstOrNull()?.innerProject?.let {
        val innerProjectComponents: InnerProjectComponents = Json.decodeFromString(it.jsonData)
        innerProjectComponents.videos.firstOrNull()?.filePath ?: ""
    } ?: ""

    return InterviewGroup(
        id = id,
        createdAt = ZonedDateTime.parse(createdAt),
        recordState = RecordState.stringToRecordState(recordState),
        questionCount = interviews.size,
        thumbnailVideoPath = thumbnailVideoPath
    )
}