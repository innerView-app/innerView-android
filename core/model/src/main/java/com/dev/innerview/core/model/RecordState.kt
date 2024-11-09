package com.dev.innerview.core.model

enum class RecordState {
    IDLE,
    PAUSE,
    RECODING,
    COMPLETE;

    companion object {
        fun stringToRecordState(value: String?): RecordState {
            return entries.find { it.name == value } ?: RECODING
        }
    }
}