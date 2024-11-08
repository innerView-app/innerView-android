package com.dev.innerview.core.data.mapper

import com.dev.innerview.core.database.schema.InterviewGroupSchema
import com.dev.innerview.core.model.InterviewGroup
import com.dev.innerview.core.model.RecordState
import java.time.ZonedDateTime

internal fun InterviewGroupSchema.toData(): InterviewGroup =
    InterviewGroup(
        id = id,
        createdAt = ZonedDateTime.parse(createdAt),
        recordState = RecordState.stringToRecordState(recordState),
        questionCount = interviews.size,
        thumbnailVideoPath = interviews.firstNotNullOfOrNull { it.innerProject?.jsonData }
    )