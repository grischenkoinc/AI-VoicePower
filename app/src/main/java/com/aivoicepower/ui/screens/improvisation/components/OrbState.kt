package com.aivoicepower.ui.screens.improvisation.components

enum class OrbState {
    IDLE,        // Чекає на дію — повільне дихання, gradient pulse
    SPEAKING,    // AI говорить (TTS) — wave ripple від orb
    LISTENING,   // Юзер записує — бірюзовий відтінок, audio level bars
    THINKING,    // AI думає — shimmer/обертання
    COMPLETE     // Завершено — зелений glow
}
