package com.dev.innerview.feature.edit.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ViewModelComponent::class)
object PlaybackModule {

    @Provides
    @ViewModelScoped
    internal fun provideVideoPlayer(
        @ApplicationContext context: Context,
    ): Player {
        return ExoPlayer.Builder(context)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun scope(): CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
}