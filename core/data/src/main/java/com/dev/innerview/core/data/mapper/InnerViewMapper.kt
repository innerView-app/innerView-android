package com.dev.innerview.core.data.mapper

import com.dev.innerview.core.database.schema.InnerViewSchema
import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewType
import java.time.ZonedDateTime

internal fun InnerViewSchema.toData(): InnerView =
    InnerView(
        id = _id,
        title = title,
        type = InnerViewType.stringToInnerViewType(type),
        createdAt = ZonedDateTime.parse(createdAt),
        questions = questions
    )