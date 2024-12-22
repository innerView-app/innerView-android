package com.dev.innerview.core.data.repository

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.database.datasource.InnerViewDataSource
import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewContent
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
                        questions = innerViewSchema.questions,
                        isNotificationOn = innerViewSchema.isNotificationOn
                    )
                }
            }

    override fun getInnerViewContent(innerViewId: String): Flow<InnerViewContent> =
        innerViewDataSource.getInnerViewById(innerViewId)
            .map { innerView ->
                val interviewGroups = innerView.interviewGroups
                    .map { interviewGroupSchema ->
                        val videoPaths =
                            interviewGroupSchema.interviews.mapNotNull { it.innerProject?.videoPath }
                        val thumbnailVideoPath = videoPaths.firstOrNull()

                        InterviewGroup(
                            id = interviewGroupSchema.id,
                            createdAt = ZonedDateTime.parse(interviewGroupSchema.createdAt),
                            recordState = RecordState.stringToRecordState(interviewGroupSchema.recordState),
                            questionCount = interviewGroupSchema.interviews.size,
                            thumbnailVideoPath = thumbnailVideoPath
                        )
                    }.sortedByDescending { it.createdAt }

                InnerViewContent(
                    InnerView(
                        id = innerViewId,
                        title = innerView.title,
                        type = InnerViewType.stringToInnerViewType(innerView.type),
                        createdAt = ZonedDateTime.parse(innerView.createdAt),
                        questions = innerView.questions,
                        isNotificationOn = innerView.isNotificationOn
                    ),
                    interviewGroups
                )
            }

    override fun getInterviews(
        innerViewId: String,
        interviewGroupId: Int
    ): Flow<List<Interview>> =
        innerViewDataSource.getInnerViewById(innerViewId)
            .map { innerView ->

                val interviewGroups =
                    innerView.interviewGroups

                val interviews =
                    interviewGroups.firstOrNull { it.id == interviewGroupId }?.interviews

                interviews?.map { interviewSchema ->
                    Interview(
                        createdAt = interviewSchema.createdAt?.let { ZonedDateTime.parse(it) },
                        question = interviewSchema.question,
                        isRequired = interviewSchema.isRequired,
                        isRecordComplete = interviewSchema.innerProject != null,
                        thumbnailVideoPath = interviewSchema.innerProject?.videoPath
                    )
                } ?: listOf()
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

    override suspend fun changeInnerViewNotification(innerViewId: String, isOn: Boolean) {
        innerViewDataSource.changeInnerViewNotification(innerViewId, isOn)
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