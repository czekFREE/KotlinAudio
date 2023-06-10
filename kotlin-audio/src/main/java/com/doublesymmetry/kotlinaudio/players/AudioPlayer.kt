package com.doublesymmetry.kotlinaudio.players

import android.content.Context
import com.doublesymmetry.kotlinaudio.models.BufferConfig
import com.doublesymmetry.kotlinaudio.models.CacheConfig
import com.doublesymmetry.kotlinaudio.models.PlayerConfig
import android.support.v4.media.session.MediaSessionCompat

class AudioPlayer(
    context: Context,
    playerConfig: PlayerConfig = PlayerConfig(),
    bufferConfig: BufferConfig? = null,
    cacheConfig: CacheConfig? = null,
    mediaSession: MediaSessionCompat = MediaSessionCompat(
        context,
        "KotlinAudioPlayer"
    )
) : BaseAudioPlayer(context, playerConfig, bufferConfig, cacheConfig, mediaSession)