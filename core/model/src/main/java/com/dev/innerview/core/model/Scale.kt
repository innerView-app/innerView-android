package com.dev.innerview.core.model

enum class Scale(val text: String) {
    SIZE_480P("480p (480x854)"),
    SIZE_720P("720p (720x1280)"),
    SIZE_1080P("1080p (1080x1920)");

    companion object {
        fun stringToScale(value: String?): Scale {
            return Scale.entries.find { it.name == value } ?: SIZE_1080P
        }
    }
}