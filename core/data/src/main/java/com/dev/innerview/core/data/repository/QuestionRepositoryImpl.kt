package com.dev.innerview.core.data.repository

import android.content.Context
import com.dev.innerview.core.data_api.QuestionRepository
import com.dev.innerview.core.model.InnerViewType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : QuestionRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override fun getRecommendQuestionsByType(type: InnerViewType): List<String> {

        val jsonFileName = when (type) {
            InnerViewType.YEAR -> "questions_year.json"
            InnerViewType.MONTH -> "questions_month.json"
            InnerViewType.WEEK -> "questions_week.json"
            InnerViewType.DAY -> "questions_day.json"
        }

        val inputStream = context.assets.open(jsonFileName)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val questions = json.decodeFromString<List<String>>(jsonString)

        return questions
    }
}