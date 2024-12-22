package com.dev.innerview.feature.notification.di

import android.content.Context
import com.dev.innerview.core.domain.usecase.GetInnerViewContentUseCase
import com.dev.innerview.core.domain.usecase.GetInnerViewUseCase
import com.dev.innerview.feature.notification.AlarmHelper
import com.dev.innerview.feature.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Singleton
    @Provides
    fun provideAlarmHelper(
        @ApplicationContext context: Context,
        getInnerViewUseCase: GetInnerViewUseCase,
        getInnerViewContentUseCase: GetInnerViewContentUseCase
    ) = AlarmHelper(context, getInnerViewUseCase, getInnerViewContentUseCase)

    @Singleton
    @Provides
    fun provideNotificationHelper(@ApplicationContext context: Context) =
        NotificationHelper(context)
}