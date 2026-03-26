package com.aivoicepower.audio

import com.aivoicepower.R

enum class SoundCategory {
    UI,          // Tab switch, page change — subtle
    FEEDBACK,    // Recording, analysis, timer — moderate
    CELEBRATION  // Achievements, fanfares — prominent
}

enum class SoundEffect(
    val resId: Int,
    val category: SoundCategory,
    val volumeMultiplier: Float = 1.0f
) {
    // Tier 1 — Critical
    RECORD_START(R.raw.sound_record_start, SoundCategory.FEEDBACK, 0.4f),
    RECORD_STOP(R.raw.sound_record_stop, SoundCategory.FEEDBACK, 0.5f),
    COUNTDOWN_TICK(R.raw.sound_countdown_tick, SoundCategory.FEEDBACK, 0.3f),
    COUNTDOWN_GO(R.raw.sound_countdown_go, SoundCategory.FEEDBACK, 0.3f),
    ANALYSIS_SUCCESS(R.raw.sound_analysis_success, SoundCategory.FEEDBACK, 0.64f),
    ANALYSIS_ERROR(R.raw.sound_analysis_error, SoundCategory.FEEDBACK, 0.25f),
    LESSON_COMPLETED(R.raw.sound_lesson_completed, SoundCategory.CELEBRATION, 0.09f),
    CELEBRATION(R.raw.sound_celebration, SoundCategory.CELEBRATION, 0.175f),

    // Tier 2 — Important feedback
    TIMER_URGENT(R.raw.sound_timer_urgent, SoundCategory.FEEDBACK),
    EXERCISE_COMPLETED(R.raw.sound_exercise_completed, SoundCategory.FEEDBACK, 0.25f),
    COURSE_COMPLETED(R.raw.sound_course_completed, SoundCategory.CELEBRATION),
    COACH_PING(R.raw.sound_coach_ping, SoundCategory.FEEDBACK),
    RECORDING_SAVED(R.raw.sound_recording_saved, SoundCategory.FEEDBACK),
    LIMIT_REACHED(R.raw.sound_limit_reached, SoundCategory.FEEDBACK),
    AD_REWARD(R.raw.sound_ad_reward, SoundCategory.FEEDBACK),
    WARMUP_COMPLETED(R.raw.sound_warmup_completed, SoundCategory.CELEBRATION),

    // Tier 3 — Polish
    TAB_SWITCH(R.raw.sound_tab_switch, SoundCategory.UI, 0.04f),
    BREATH_INHALE(R.raw.sound_breath_inhale, SoundCategory.FEEDBACK, 0.18f),
    BREATH_EXHALE(R.raw.sound_breath_exhale, SoundCategory.FEEDBACK, 0.10f),
    ONBOARDING_PAGE(R.raw.sound_onboarding_page, SoundCategory.UI),
    SPLASH_BRAND(R.raw.sound_splash_brand, SoundCategory.CELEBRATION, 0.7f),
    PREMIUM_UNLOCKED(R.raw.sound_premium_unlocked, SoundCategory.CELEBRATION),
    SKILL_IMPROVED(R.raw.sound_skill_improved, SoundCategory.FEEDBACK)
}
