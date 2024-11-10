package com.dev.innerview.core.data.di

import com.dev.innerview.core.data.repository.InnerViewRepositoryImpl
import com.dev.innerview.core.data.repository.QuestionRepositoryImpl
import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.data_api.QuestionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindsSettingsRepository(
        repository: InnerViewRepositoryImpl,
    ): InnerViewRepository

    @Binds
    @Singleton
    abstract fun bindQuestionRepository(
        questionRepositoryImpl: QuestionRepositoryImpl
    ): QuestionRepository
}