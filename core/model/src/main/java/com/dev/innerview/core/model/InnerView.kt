package com.dev.innerview.core.model

import java.time.ZonedDateTime

data class InnerView(
    val id: String = "",
    val title: String = "",
    val type: InnerViewType = InnerViewType.YEAR,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val questions: List<String> = listOf()
)