package com.dev.innerview.core.database.datasource

import com.dev.innerview.core.database.schema.InnerViewSchema
import com.dev.innerview.core.database.schema.ProjectSchema
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.find
import kotlinx.coroutines.flow.map
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
        val id = getNextPrimaryKey()
        val newInnerView = InnerViewSchema().apply {
            this._id = id
            this.title = title
            this.type = type
            this.createdAt = ZonedDateTime.now(ZoneOffset.UTC).toString()
        }

        realm.write {
            copyToRealm(newInnerView)
        }
    }

    suspend fun deleteInnerView(id: Int) {
        realm.write {
            val innerViewDelete = query<InnerViewSchema>("_id == $0", id).find().first()
            delete(innerViewDelete)
        }
    }

    private fun getNextPrimaryKey(): Int {
        return realm.query<InnerViewSchema>().max("_id", Int::class).find { i ->
            if (i == null) {
                1
            } else {
                i + 1
            }
        }
    }
}