package com.dev.innerview.core.database.datasource

import com.dev.innerview.core.database.schema.InnerProjectSchema
import com.dev.innerview.core.database.schema.InnerViewSchema
import com.dev.innerview.core.database.schema.InterviewGroupSchema
import com.dev.innerview.core.database.schema.InterviewSchema
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject

class InnerViewDataSource @Inject constructor(
    private val realm: Realm
) {

    val innerViewData = realm
        .query<InnerViewSchema>()
        .asFlow()
        .map { result ->
            result.list.toList()
        }

    val projectData = realm
        .query<InnerProjectSchema>()
        .asFlow()
        .map { result ->
            result.list.toList()
        }

    suspend fun addInnerView(
        title: String,
        type: String,
    ) {
        val createAt = ZonedDateTime.now(ZoneOffset.UTC).toString()
        val id = sha256(title + createAt)

        realm.write {
            copyToRealm(
                InnerViewSchema().apply {
                    this._id = id
                    this.title = title
                    this.type = type
                    this.createdAt = createAt
                }
            )
        }
    }

    suspend fun deleteInnerView(id: String) {
        realm.write {
            val innerView = query<InnerViewSchema>("_id == $0", id).find().first()

            innerView.interviewGroups.forEach { interviewGroup ->
                interviewGroup.interviews.forEach { interview ->
                    interview.innerProject?.let { delete(it) }
                }
            }

            delete(innerView)
        }
    }

    suspend fun addInterviewGroup(innerViewId: String) {
        realm.write {

            val innerView = query<InnerViewSchema>("_id == $0", innerViewId).find().first()

            val interviews = if (innerView.interviewGroups.isEmpty()) {
                realmListOf()
            } else {
                innerView.interviewGroups.last().interviews.map { prevInterview ->
                    InterviewSchema().apply {
                        this.question = prevInterview.question
                        this.isRequired = true
                    }
                }.toRealmList()
            }

            val newInterviewGroupSchema = InterviewGroupSchema().apply {
                this.id = innerView.interviewGroups.size + 1
                this.createdAt = ZonedDateTime.now(ZoneOffset.UTC).toString()
                this.recordState = "RECODING"
                this.interviews = interviews
            }

            innerView.interviewGroups.add(newInterviewGroupSchema)
        }
    }

    suspend fun deleteInterviewGroup(
        innerViewId: String,
        interviewGroupId: Int,
    ) {
        realm.write {
            val interviewGroup = query<InnerViewSchema>("_id == $0", innerViewId).find().first()
                .interviewGroups.first { it.id == interviewGroupId }

            interviewGroup.interviews.forEach { interview ->
                interview.innerProject?.let { delete(it) }
            }

            delete(interviewGroup)
        }
    }

    suspend fun addQuestion(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    ) {
        realm.write {
            val innerView = query<InnerViewSchema>("_id == $0", innerViewId).find().first()

            innerView.interviewGroups.first { it.id == interviewGroupId }.interviews.add(
                InterviewSchema().apply {
                    this.question = question
                    this.isRequired = false
                }
            )
        }
    }

    suspend fun deleteQuestion(
        innerViewId: String,
        interviewGroupId: Int,
        question: String
    ) {
        realm.write {
            val innerView = query<InnerViewSchema>("_id == $0", innerViewId).find().first()

            val interview = innerView.interviewGroups.first { it.id == interviewGroupId }
                .interviews.first { it.question == question }

            if (!interview.isRequired) {
                interview.innerProject?.let { delete(it) }
                delete(interview)
            }
        }
    }

    suspend fun addInnerProject(
        innerViewId: String,
        interviewGroupId: Int,
        question: String,
    ) {
        realm.write {
            val innerView = query<InnerViewSchema>("_id == $0", innerViewId).find().first()

            val interview = innerView.interviewGroups.first { it.id == interviewGroupId }
                .interviews.first { it.question == question }

            interview.createdAt = ZonedDateTime.now(ZoneOffset.UTC).toString()
            interview.innerProject = InnerProjectSchema().apply {
                this.innerViewId = innerViewId
                this.interviewGroupId = interviewGroupId
                this.recordState = "RECODING"
                this.videoPath = "input.mp4"
            }
        }
    }

    suspend fun deleteInnerProject(
        innerViewId: String,
        interviewGroupId: Int,
        question: String,
    ) {
        realm.write {
            val innerView = query<InnerViewSchema>("_id == $0", innerViewId).find().first()

            val innerProject = innerView.interviewGroups.first { it.id == interviewGroupId }
                .interviews.first { it.question == question }.innerProject

            innerProject?.let { delete(it) }
        }
    }

    suspend fun completeInterviewGroup(
        innerViewId: String,
        interviewGroupId: Int
    ) {
        realm.write {
            val innerView = query<InnerViewSchema>("_id == $0", innerViewId).find().first()

            val interviewGroup = innerView.interviewGroups.first { it.id == interviewGroupId }

            if (interviewGroup.interviews.all { it.innerProject != null }) {

                interviewGroup.interviews
                    .filter { !it.isRequired }
                    .map { it.question }
                    .forEach { question ->
                        innerView.questions.add(question)
                    }

                interviewGroup.recordState = "COMPLETE"
                interviewGroup.interviews.forEach { interview ->
                    interview.innerProject?.recordState = "COMPLETE"
                }

            }
        }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}