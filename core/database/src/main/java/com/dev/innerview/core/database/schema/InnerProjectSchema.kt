package com.dev.innerview.core.database.schema

import io.realm.kotlin.types.RealmObject

class InnerProjectSchema : RealmObject {
    var innerViewId: String? = null
    var interviewGroupId: Int? = null
    var recordState: String = ""
    var videoPath: String = ""
}