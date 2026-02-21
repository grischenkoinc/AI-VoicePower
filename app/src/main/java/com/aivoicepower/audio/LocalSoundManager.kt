package com.aivoicepower.audio

import androidx.compose.runtime.staticCompositionLocalOf

val LocalSoundManager = staticCompositionLocalOf<SoundManager> {
    error("SoundManager not provided")
}
