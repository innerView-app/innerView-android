package com.dev.innerview.core.model

import kotlinx.serialization.Serializable

@Serializable
data class InnerProjectComponents(
    val media: List<InterviewPiece> = listOf(),
    val subtitles: List<Subtitle> = listOf()
)

@Serializable
data class InterviewPiece(
    val filePath: String = "",
    val startPosition: Long = 0L,
    val endPosition: Long = 0L,
    val duration: Long = 0L,
)

@Serializable
data class Subtitle(
    val text: String = "",
    val startPosition: Long = 0L,
    val endPosition: Long = 0L,
    val x: Float = 0f,
    val y: Float = 0f,
    val fontSize: Float = 0f,
    val color: Int = 0,
)