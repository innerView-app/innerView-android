package com.dev.innerview.core.model

import java.time.ZonedDateTime

data class InnerView(
    val id: String,
    val title: String,
    val type: InnerViewType,
    val createdAt: ZonedDateTime,
    val questions: List<String>,
    val isNotificationOn: Boolean
)