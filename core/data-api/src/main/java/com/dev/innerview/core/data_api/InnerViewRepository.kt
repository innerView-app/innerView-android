package com.dev.innerview.core.data_api

import com.dev.innerview.core.model.InnerProject
import com.dev.innerview.core.model.InnerProjectComponents
import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewContent
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.Interview
import com.dev.innerview.core.model.InterviewGroupContent
import kotlinx.coroutines.flow.Flow

interface InnerViewRepository {

    fun getInnerViews(): Flow<List<InnerView>>

    fun getInnerViewContent(innerViewId: String): Flow<InnerViewContent>

    fun getInterviewGroupContentById(
        innerViewId: String,
        interviewGroupId: Int
    ): Flow<InterviewGroupContent>

    fun getInterviewByQuestion(innerViewId: String, question: String): Flow<List<Interview>>

    fun getInnerProject(): Flow<List<InnerProject>>

    fun getInnerProjectById(id: Int): Flow<InnerProject>

    suspend fun addInnerView(title: String, type: InnerViewType)

    suspend fun deleteInnerView(id: String)

    suspend fun addInterviewGroup(innerViewId: String, addPrevQuestions: Boolean = true)

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
        title: String,
        innerViewId: String? = null,
        interviewGroupId: Int? = null,
        innerProjectComponents: InnerProjectComponents = InnerProjectComponents()
    ): Int

    suspend fun updateInnerProject(
        innerProjectId: Int,
        innerProjectComponents: InnerProjectComponents
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