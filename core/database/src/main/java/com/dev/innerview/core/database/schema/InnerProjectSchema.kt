package com.dev.innerview.core.database.schema

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class InnerProjectSchema : RealmObject {
    @PrimaryKey
    var _id: Int = 0
    var title: String? = null
    var innerViewId: String? = null
    var interviewGroupId: Int? = null
    var recordState: String = ""
    var jsonData: String = "[]"
}