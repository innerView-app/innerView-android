package com.dev.innerview.core.database.di

import com.dev.innerview.core.database.schema.InnerViewSchema
import com.dev.innerview.core.database.schema.InterviewGroupSchema
import com.dev.innerview.core.database.schema.InterviewSchema
import com.dev.innerview.core.database.schema.InnerProjectSchema
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRealm(): Realm {
        val config = RealmConfiguration
            .Builder(
                schema = setOf(
                    InnerViewSchema::class,
                    InterviewGroupSchema::class,
                    InterviewSchema::class,
                    InnerProjectSchema::class
                )
            ).name("innerview.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded() // develop only
            .build()

        return Realm.open(config)
    }
}