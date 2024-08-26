package com.dev.innerview.core.data_api

import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.Interview
import com.dev.innerview.core.model.InterviewGroup
import kotlinx.coroutines.flow.Flow

interface InnerViewRepository {

    fun getInnerViews(): Flow<List<InnerView>>

    fun getInterviewGroups(innerViewId: String): Flow<List<InterviewGroup>>

    fun getInterviews(innerViewId: String, interviewGroupId: String): Flow<List<Interview>>

    suspend fun addInnerView(title: String, type: InnerViewType)

    suspend fun deleteInnerView(id: String)

    suspend fun addInterviewGroup(innerViewId: String)

    suspend fun deleteInterviewGroup(
        innerViewId: String,
        interviewGroupId: String,
    )

    suspend fun addQuestion(
        innerViewId: String,
        interviewGroupId: String,
        question: String
    )

    suspend fun deleteQuestion(
        innerViewId: String,
        interviewGroupId: String,
        question: String
    )

    suspend fun addInnerProject(
        innerViewId: String,
        interviewGroupId: String,
        question: String,
    )

    suspend fun deleteInnerProject(
        innerViewId: String,
        interviewGroupId: String,
        question: String,
    )

    suspend fun completeInterviewGroup(
        innerViewId: String,
        interviewGroupId: String
    )
}