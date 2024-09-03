package com.dev.innerview.core.database.schema

import io.realm.kotlin.types.EmbeddedRealmObject

class InterviewSchema : EmbeddedRealmObject {
    var createdAt: String? = null
    var question: String = ""
    var isRequired: Boolean = true
    var innerProject: InnerProjectSchema? = null
}