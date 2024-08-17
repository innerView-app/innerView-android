package com.dev.innerview.core.model

enum class InnerViewType {
    YEAR,
    MONTH,
    WEEK,
    DAY;

    companion object {
        fun stringToInnerViewType(value: String): InnerViewType? {
            return entries.find { it.name == value }
        }
    }
}