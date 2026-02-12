package com.aivoicepower.ui.screens.tonguetwister

import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.domain.model.content.TongueTwister

data class TongueTwistersState(
    val allTongueTwisters: List<TongueTwister> = emptyList(),
    val filteredTongueTwisters: List<TongueTwister> = emptyList(),
    val selectedCategory: String? = null,
    val selectedDifficulty: Int? = null,
    val searchQuery: String = "",
    val expandedTwisterId: String? = null,
    val isPracticing: Boolean = false,
    val practicingTwister: TongueTwister? = null,
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val isAnalyzing: Boolean = false,
    val analysisResult: VoiceAnalysisResult? = null,
    // Analysis limits
    val showAnalysisLimitSheet: Boolean = false,
    val isPremium: Boolean = true,
    val remainingAnalyses: Int = Int.MAX_VALUE,
    val remainingAdAnalyses: Int = 0,
    val isAdLoaded: Boolean = false
)

sealed class TongueTwistersEvent {
    data class SelectCategory(val category: String?) : TongueTwistersEvent()
    data class SelectDifficulty(val difficulty: Int?) : TongueTwistersEvent()
    data class UpdateSearch(val query: String) : TongueTwistersEvent()
    data class ToggleExpand(val twisterId: String) : TongueTwistersEvent()
    data class StartPractice(val twister: TongueTwister) : TongueTwistersEvent()
    object StopPractice : TongueTwistersEvent()
    object StartRecording : TongueTwistersEvent()
    object StopRecording : TongueTwistersEvent()
    object DismissAnalysis : TongueTwistersEvent()
    // Analysis limits
    object DismissAnalysisLimitSheet : TongueTwistersEvent()
    object WatchAdForAnalysis : TongueTwistersEvent()
    object ContinueWithoutAnalysis : TongueTwistersEvent()
}
