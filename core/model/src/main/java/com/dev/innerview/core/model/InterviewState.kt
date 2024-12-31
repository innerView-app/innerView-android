package com.dev.innerview.core.model

enum class InterviewState {
    RECORDING,
    COMPLETE;

    companion object {
        fun stringToInterviewState(value: String?): InterviewState {
            return entries.find { it.name == value } ?: RECORDING
        }
    }
}