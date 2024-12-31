package com.dev.innerview.core.notification.di

import android.content.Context
import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.notification.AlarmHelper
import com.dev.innerview.core.notification.NotificationHelper
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
        @ApplicationContext context: Context, innerViewRepository: InnerViewRepository
    ) = AlarmHelper(context, innerViewRepository)

    @Singleton
    @Provides
    fun provideNotificationHelper(@ApplicationContext context: Context) =
        NotificationHelper(context)
}