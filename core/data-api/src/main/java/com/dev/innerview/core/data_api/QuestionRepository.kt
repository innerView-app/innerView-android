package com.dev.innerview.core.data_api

import com.dev.innerview.core.model.InnerViewType

interface QuestionRepository {
    fun getRecommendQuestionsByType(type: InnerViewType): List<String>
}