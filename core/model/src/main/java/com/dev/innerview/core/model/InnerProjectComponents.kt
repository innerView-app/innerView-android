package com.dev.innerview.core.model

import kotlinx.serialization.Serializable

@Serializable
data class InnerProjectComponents(
    val videos: List<InterviewPiece> = listOf(),
    val subtitles: List<Subtitle> = listOf()
)

@Serializable
data class InterviewPiece(
    val filePath: String,
    val startPosition: Long,
    val endPosition: Long,
    val duration: Long,
)

@Serializable
data class Subtitle(
    val text: String
)