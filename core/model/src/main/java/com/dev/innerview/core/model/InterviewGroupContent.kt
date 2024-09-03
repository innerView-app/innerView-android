package com.dev.innerview.core.model

data class InterviewGroupContent(
    val innerView: InnerView,
    val interviewGroup: InterviewGroup,
    val interviews: List<Interview>
)