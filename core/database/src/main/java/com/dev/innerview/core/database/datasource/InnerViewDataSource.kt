package com.dev.innerview.core.database.datasource

import com.dev.innerview.core.database.schema.InnerViewSchema
import com.dev.innerview.core.database.schema.ProjectSchema
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
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
        .query<ProjectSchema>()
        .asFlow()
        .map { result ->
            result.list.toList()
        }

    suspend fun addInnerView(
        title: String,
        type: String,
    ) {
        val createAt = ZonedDateTime.now(ZoneOffset.UTC).toString()
        val id = getInnerViewPrimaryKey(title, createAt)
        val newInnerView = InnerViewSchema().apply {
            this._id = id
            this.title = title
            this.type = type
            this.createdAt = createAt
        }

        realm.write {
            copyToRealm(newInnerView)
        }
    }

    suspend fun deleteInnerView(id: String) {
        realm.write {
            val innerViewDelete = query<InnerViewSchema>("_id == $0", id).find().first()
            delete(innerViewDelete)
        }
    }

    private fun getInnerViewPrimaryKey(title: String, createAt: String): String {
        return sha256(title + createAt)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}