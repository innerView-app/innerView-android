package com.dev.innerview.core.database.di

import com.dev.innerview.core.database.schema.InnerViewSchema
import com.dev.innerview.core.database.schema.InterviewGroupSchema
import com.dev.innerview.core.database.schema.InterviewSchema
import com.dev.innerview.core.database.schema.ProjectSchema
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
                    ProjectSchema::class
                )
            ).name("innerview.realm")
            .build()

        return Realm.open(config)
    }
}