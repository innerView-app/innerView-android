package com.dev.innerview.core.data.repository

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.database.datasource.InnerViewDataSource
import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewType
import com.dev.innerview.core.model.Interview
import com.dev.innerview.core.model.InterviewGroup
import com.dev.innerview.core.model.RecordState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject

class InnerViewRepositoryImpl @Inject constructor(
    private val innerViewDataSource: InnerViewDataSource
) : InnerViewRepository {

    override fun getInnerViews(): Flow<List<InnerView>> =
        innerViewDataSource.innerViewData
            .map { innerViewList ->
                innerViewList.map { innerViewSchema ->
                    InnerView(
                        id = innerViewSchema._id,
                        title = innerViewSchema.title,
                        type = InnerViewType.stringToInnerViewType(innerViewSchema.type),
                        createdAt = ZonedDateTime.parse(innerViewSchema.createdAt),
                        questions = innerViewSchema.questions
                    )
                }
            }

    override fun getInterviewGroups(innerViewId: String): Flow<List<InterviewGroup>> =
        innerViewDataSource.innerViewData
            .map { innerViewList ->
                innerViewList.first { it._id == innerViewId }
                    .interviewGroups.map { interviewGroupSchema ->
                        InterviewGroup(
                            id = interviewGroupSchema.id,
                            createdAt = ZonedDateTime.parse(interviewGroupSchema.createdAt),
                            recordState = RecordState.stringToInnerViewType(interviewGroupSchema.recordState),
                            questionCount = interviewGroupSchema.interviews.size,
                            thumbnailVideoPath = interviewGroupSchema.interviews.first().innerProject?.videoPath
                        )
                    }
            }

    override fun getInterviews(
        innerViewId: String,
        interviewGroupId: Int
    ): Flow<List<Interview>> =
        innerViewDataSource.innerViewData
            .map { innerViewList ->
                innerViewList.first { it._id == innerViewId }
                    .interviewGroups.first { it.id == interviewGroupId }.interviews.map { interviewSchema ->
                        Interview(
                            createdAt = ZonedDateTime.parse(interviewSchema.createdAt),
                            question = interviewSchema.question,
                            isRequired = interviewSchema.isRequired,
                            recordState = RecordState.stringToInnerViewType(interviewSchema.innerProject?.recordState),
                            thumbnailVideoPath = interviewSchema.innerProject?.videoPath
                        )
                    }
            }


    override suspend fun addInnerView(title: String, type: InnerViewType) {
        innerViewDataSource.addInnerView(title, type.name)
    }

    override suspend fun deleteInnerView(id: String) {
        innerViewDataSource.deleteInnerView(id)
    }

    override suspend fun addInterviewGroup(innerViewId: String) {
        innerViewDataSource.addInterviewGroup(innerViewId)
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

    override suspend fun deleteQuestion(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    ) {
        innerViewDataSource.deleteQuestion(innerViewId, interviewGroupId, question)
    }

    override suspend fun addInnerProject(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    ) {
        innerViewDataSource.addInnerProject(innerViewId, interviewGroupId, question)
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