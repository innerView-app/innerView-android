package com.dev.innerview.core.data_api

import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewContent
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.Interview
import kotlinx.coroutines.flow.Flow

interface InnerViewRepository {

    fun getInnerViews(): Flow<List<InnerView>>

    fun getInnerViewContent(innerViewId: String): Flow<InnerViewContent>

    fun getInterviews(innerViewId: String, interviewGroupId: Int): Flow<List<Interview>>

    suspend fun addInnerView(title: String, type: InnerViewType)

    suspend fun deleteInnerView(id: String)

    suspend fun addInterviewGroup(innerViewId: String)

    suspend fun changeInnerViewNotification(
        innerViewId: String,
        isOn: Boolean
    )

    suspend fun deleteInterviewGroup(
        innerViewId: String,
        interviewGroupId: Int,
    )

    suspend fun addQuestion(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    )

    suspend fun reorderQuestions(
        innerViewId: String,
        oldIndex: Int,
        newIndex: Int
    )

    suspend fun deleteQuestion(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    )

    suspend fun addInnerProject(
        innerViewId: String,
        interviewGroupId: Int,
        question: String,
    )

    suspend fun deleteInnerProject(
        innerViewId: String,
        interviewGroupId: Int,
        question: String,
    )

    suspend fun completeInterviewGroup(
        innerViewId: String,
        interviewGroupId: Int
    )
}