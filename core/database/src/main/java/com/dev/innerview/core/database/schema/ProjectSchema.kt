package com.dev.innerview.core.database.schema

import io.realm.kotlin.types.RealmObject

class ProjectSchema : RealmObject {
    var innerViewId: Int? = null
    var videoPath: String = ""
}