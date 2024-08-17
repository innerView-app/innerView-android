package com.dev.innerview.core.model

import java.time.ZonedDateTime

data class InnerView(
    val id: Int,
    val title: String,
    val type: InnerViewType,
    val createdAt: ZonedDateTime,
)