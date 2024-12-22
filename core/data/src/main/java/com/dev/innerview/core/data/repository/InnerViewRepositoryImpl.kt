package com.dev.innerview.core.data.repository

import com.dev.innerview.core.data.mapper.toData
import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.database.datasource.InnerViewDataSource
import com.dev.innerview.core.model.InnerProject
import com.dev.innerview.core.model.InnerProjectComponents
import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewContent
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.Interview
import com.dev.innerview.core.model.InterviewGroupContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class InnerViewRepositoryImpl @Inject constructor(
    private val innerViewDataSource: InnerViewDataSource
) : InnerViewRepository {

    override fun getInnerViews(): Flow<List<InnerView>> =
        innerViewDataSource.innerViewData
            .map { innerViews ->
                innerViews.map { innerView ->
                    innerView.toData()
                }
            }

    override fun getInnerViewContent(innerViewId: String): Flow<InnerViewContent> =
        innerViewDataSource.getInnerViewById(innerViewId)
            .map { innerView ->
                val interviewGroups = innerView.interviewGroups

                InnerViewContent(
                    innerView = innerView.toData(),
                    interviewGroups = interviewGroups.map { it.toData() }
                        .sortedByDescending { it.createdAt }
                )
            }

    override fun getInterviewGroupContentById(
        innerViewId: String,
        interviewGroupId: Int
    ): Flow<InterviewGroupContent> =
        innerViewDataSource.getInnerViewById(innerViewId)
            .map { innerView ->

                val interviewGroups =
                    innerView.interviewGroups.firstOrNull { it.id == interviewGroupId }
                        ?: throw IllegalArgumentException()

                InterviewGroupContent(
                    innerView = innerView.toData(),
                    interviewGroup = interviewGroups.toData(),
                    interviews = interviewGroups.interviews.map { interviewSchema ->
                        interviewSchema.toData()
                    }
                )
            }

    override fun getInterviewByQuestion(
        innerViewId: String,
        question: String
    ): Flow<List<Interview>> =
        innerViewDataSource.getInnerViewById(innerViewId)
            .map { innerView ->
                innerView.interviewGroups.flatMap { it.interviews }
                    .filter { it.question == question }.map { it.toData() }
            }

    override fun getInnerProject(): Flow<List<InnerProject>> =
        innerViewDataSource.projectData
            .map { innerProjects ->
                innerProjects.filter { it.innerViewId == null && it.interviewGroupId == null }.map {
                    it.toData()
                }
            }

    override fun getInnerProjectById(id: Int): Flow<InnerProject> =
        innerViewDataSource.getInnerProjectById(id)
            .map { innerProject ->
                innerProject.toData()
            }

    override suspend fun addInnerView(title: String, type: InnerViewType) {
        innerViewDataSource.addInnerView(title, type.name)
    }

    override suspend fun deleteInnerView(id: String) {
        innerViewDataSource.deleteInnerView(id)
    }

    override suspend fun addInterviewGroup(innerViewId: String, addPrevQuestions: Boolean) {
        innerViewDataSource.addInterviewGroup(innerViewId, addPrevQuestions)
    }

    override suspend fun deleteInterviewGroup(innerViewId: String, interviewGroupId: Int) {
        innerViewDataSource.deleteInterviewGroup(innerViewId, interviewGroupId)
    }

    override suspend fun addQuestion(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    ) {
        innerViewDataSource.addQuestion(innerViewId, interviewGroupId, question)
    }

    override suspend fun reorderQuestions(innerViewId: String, oldIndex: Int, newIndex: Int) {
        innerViewDataSource.reorderQuestions(innerViewId, oldIndex, newIndex)
    }

    override suspend fun deleteQuestion(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    ) {
        innerViewDataSource.deleteQuestion(innerViewId, interviewGroupId, question)
    }

    override suspend fun addInnerProject(
        title: String,
        innerViewId: String?,
        interviewGroupId: Int?,
        innerProjectComponents: InnerProjectComponents
    ): Int {
        val jsonData = Json.encodeToString(innerProjectComponents)
        return innerViewDataSource.addInnerProject(title, innerViewId, interviewGroupId, jsonData)
    }

    override suspend fun deleteInnerProject(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    ) {
        innerViewDataSource.deleteInnerProject(innerViewId, interviewGroupId, question)
    }

    override suspend fun completeInterviewGroup(innerViewId: String, interviewGroupId: Int) {
        innerViewDataSource.completeInterviewGroup(innerViewId, interviewGroupId)
    }
}