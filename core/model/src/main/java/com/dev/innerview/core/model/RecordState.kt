package com.dev.innerview.core.model

enum class RecordState {
    IDLE,
    PAUSE,
    RECORDING,
    COMPLETE;

    companion object {
        fun stringToRecordState(value: String?): RecordState {
            return entries.find { it.name == value } ?: RECORDING
        }
    }
}