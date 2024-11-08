package com.dev.innerview.core.data.mapper

import com.dev.innerview.core.database.schema.InnerProjectSchema
import com.dev.innerview.core.model.InnerProject

internal fun InnerProjectSchema.toData(): InnerProject =
    InnerProject(
        id = _id,
        title = title,
        innerViewId = innerViewId,
        interviewGroupId = interviewGroupId,
        recordState = recordState,
        jsonData = jsonData
    )