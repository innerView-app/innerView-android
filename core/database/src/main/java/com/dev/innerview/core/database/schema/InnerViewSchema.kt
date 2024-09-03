package com.dev.innerview.core.database.schema

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class InnerViewSchema : RealmObject {
    @PrimaryKey
    var _id: String = ""
    var title: String = ""
    var type: String = ""
    var createdAt: String = ""
    var questions: RealmList<String> = realmListOf()
    var interviewGroups: RealmList<InterviewGroupSchema> = realmListOf()
}