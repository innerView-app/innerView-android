package com.dev.innerview.core.model

enum class RecordState {
    RECODING,
    COMPLETE;

    companion object {
        fun stringToInnerViewType(value: String?): RecordState {
            return entries.find { it.name == value } ?: RECODING
        }
    }
}