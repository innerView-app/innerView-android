package com.dev.innerview.core.playback.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
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

    @OptIn(UnstableApi::class)
    @Provides
    @ViewModelScoped
    internal fun provideVideoPlayer(
        @ApplicationContext context: Context,
    ): Player {
        return ExoPlayer.Builder(context)
            .setSeekParameters(SeekParameters.EXACT)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun scope(): CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
}