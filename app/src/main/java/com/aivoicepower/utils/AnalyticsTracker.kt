package com.aivoicepower.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor() {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    // ===== User Properties =====

    fun setUserProperties(isPremium: Boolean, accountType: String) {
        analytics.setUserProperty("is_premium", isPremium.toString())
        analytics.setUserProperty("account_type", accountType)
    }

    fun setIsPremium(isPremium: Boolean) {
        analytics.setUserProperty("is_premium", isPremium.toString())
    }

    // ===== Exercise Events =====

    fun logExerciseStarted(exerciseType: String, source: String, isPremium: Boolean) {
        analytics.logEvent("exercise_started", Bundle().apply {
            putString("exercise_type", exerciseType)
            putString("source", source)
            putBoolean("is_premium", isPremium)
        })
    }

    fun logRecordingCompleted(exerciseType: String, durationMs: Long, isPremium: Boolean) {
        analytics.logEvent("recording_completed", Bundle().apply {
            putString("exercise_type", exerciseType)
            putLong("duration_ms", durationMs)
            putDouble("duration_minutes", durationMs / 60_000.0)
            putBoolean("is_premium", isPremium)
        })
    }

    // ===== AI Analysis Events =====

    fun logAiAnalysisRequested(exerciseType: String, durationMs: Long, isPremium: Boolean, isAdBased: Boolean) {
        analytics.logEvent("ai_analysis_requested", Bundle().apply {
            putString("exercise_type", exerciseType)
            putLong("duration_ms", durationMs)
            putBoolean("is_premium", isPremium)
            putBoolean("is_ad_based", isAdBased)
        })
    }

    fun logAiAnalysisCompleted(
        exerciseType: String,
        durationMs: Long,
        inputTokens: Int,
        outputTokens: Int,
        latencyMs: Long,
        isPremium: Boolean
    ) {
        analytics.logEvent("ai_analysis_completed", Bundle().apply {
            putString("exercise_type", exerciseType)
            putLong("duration_ms", durationMs)
            putInt("input_tokens", inputTokens)
            putInt("output_tokens", outputTokens)
            putInt("total_tokens", inputTokens + outputTokens)
            putLong("latency_ms", latencyMs)
            putBoolean("is_premium", isPremium)
        })
    }

    // ===== Gemini Generation Events (for TTS text) =====

    /**
     * Gemini згенерував текст, який потім піде на TTS.
     * context: "debate", "sales", "interview", "presentation", "negotiation", "coach", "quick_actions"
     */
    fun logGeminiGeneration(
        context: String,
        inputTokens: Int,
        outputTokens: Int,
        isPremium: Boolean
    ) {
        analytics.logEvent("gemini_generation", Bundle().apply {
            putString("context", context)
            putInt("input_tokens", inputTokens)
            putInt("output_tokens", outputTokens)
            putInt("total_tokens", inputTokens + outputTokens)
            putBoolean("is_premium", isPremium)
        })
    }

    // ===== TTS Events =====

    fun logTtsSynthesized(charCount: Int, context: String, isPremium: Boolean) {
        analytics.logEvent("tts_synthesized", Bundle().apply {
            putInt("char_count", charCount)
            putString("context", context)
            putBoolean("is_premium", isPremium)
        })
    }

    // ===== Coach Events =====

    fun logCoachMessageSent(isPremium: Boolean) {
        analytics.logEvent("coach_message_sent", Bundle().apply {
            putBoolean("is_premium", isPremium)
        })
    }

    // ===== Monetization Events =====

    fun logAdWatched(adType: String, completed: Boolean) {
        analytics.logEvent("ad_watched", Bundle().apply {
            putString("ad_type", adType)
            putBoolean("completed", completed)
        })
    }

    fun logLimitReached(limitType: String, isPremium: Boolean) {
        analytics.logEvent("limit_reached", Bundle().apply {
            putString("limit_type", limitType)
            putBoolean("is_premium", isPremium)
        })
    }

    fun logPremiumPromptShown(trigger: String) {
        analytics.logEvent("premium_prompt_shown", Bundle().apply {
            putString("trigger", trigger)
        })
    }

    // ===== Onboarding & Auth Events =====

    fun logOnboardingStep(step: Int, stepName: String) {
        analytics.logEvent("onboarding_step", Bundle().apply {
            putInt("step", step)
            putString("step_name", stepName)
        })
    }

    fun logAuthAction(method: String, result: String) {
        analytics.logEvent("auth_action", Bundle().apply {
            putString("method", method)
            putString("result", result)
        })
    }

    // ===== AI Content Reporting =====

    fun logAiContentReported(contentType: String, reason: String) {
        analytics.logEvent("ai_content_reported", Bundle().apply {
            putString("content_type", contentType)
            putString("reason", reason)
        })
    }

    // ===== Screen Tracking =====

    fun logScreenView(screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        })
    }
}
