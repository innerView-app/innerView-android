package com.dev.innerview.core.model

data class InnerProject(
    val id: Int = 0,
    val title: String = "",
    val innerViewId: String? = null,
    val interviewGroupId: Int? = null,
    val recordState: RecordState = RecordState.RECORDING,
    val innerProjectComponents: InnerProjectComponents = InnerProjectComponents()
)