package com.dev.innerview.core.database.schema

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList

class InterviewGroupSchema : EmbeddedRealmObject {
    var createdAt: String = ""
    var interviews: RealmList<InterviewSchema> = realmListOf()
}