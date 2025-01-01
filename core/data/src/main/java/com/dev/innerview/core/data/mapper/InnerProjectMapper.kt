package com.dev.innerview.core.data.mapper

import com.dev.innerview.core.database.schema.InnerProjectSchema
import com.dev.innerview.core.model.InnerProject
import com.dev.innerview.core.model.InnerProjectComponents
import kotlinx.serialization.json.Json

internal fun InnerProjectSchema.toData(): InnerProject {
    val innerProjectComponents: InnerProjectComponents = Json.decodeFromString(jsonData)
    return InnerProject(
        id = _id,
        title = title,
        innerViewId = innerViewId,
        interviewGroupId = interviewGroupId,
        isRecording = isRecording,
        innerProjectComponents = innerProjectComponents
    )
}