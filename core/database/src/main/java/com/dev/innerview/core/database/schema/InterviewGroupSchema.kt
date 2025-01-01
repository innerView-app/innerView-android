package com.dev.innerview.core.database.schema

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList

class InterviewGroupSchema : EmbeddedRealmObject {
    var id: Int = 0
    var createdAt: String = ""
    var isRecording: Boolean = false
    var interviews: RealmList<InterviewSchema> = realmListOf()
}