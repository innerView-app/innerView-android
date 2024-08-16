package com.dev.innerview.core.data.di

import com.dev.innerview.core.data.repository.InnerViewRepositoryImpl
import com.dev.innerview.core.data_api.InnerViewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsSettingsRepository(
        repository: InnerViewRepositoryImpl,
    ): InnerViewRepository

//    @Provides
//    @Singleton
//    fun providesInnerViewRepository(
//        innerViewDataSource: InnerViewDataSource
//    ): InnerViewRepository =
//        InnerViewRepositoryImpl(innerViewDataSource)

}