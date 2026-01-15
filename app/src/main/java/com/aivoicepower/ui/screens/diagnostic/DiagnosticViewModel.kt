package com.aivoicepower.ui.screens.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.ai.GeminiAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticViewModel @Inject constructor(
    private val geminiAnalyzer: GeminiAnalyzer
) : ViewModel() {

    fun analyzeDiagnostic(
        recordingPaths: List<String>,
        onSuccess: (DiagnosticResult) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // TODO: Convert audio to text using Speech-to-Text
                val transcripts = recordingPaths.map { path ->
                    // Mock transcription for now
                    "Transcript of $path"
                }

                // Analyze with Gemini
                val prompt = buildDiagnosticPrompt(transcripts)
                val response = geminiAnalyzer.analyzeText(prompt)

                // Parse response into DiagnosticResult
                val result = parseDiagnosticResponse(response)
                onSuccess(result)

            } catch (e: Exception) {
                onError(e.message ?: "Помилка аналізу")
            }
        }
    }

    private fun buildDiagnosticPrompt(transcripts: List<String>): String {
        return """
            Проаналізуй діагностику мовлення користувача за 4 завданнями.

            Завдання 1 (Читання): ${transcripts.getOrNull(0) ?: "Немає запису"}
            Завдання 2 (Дикція - скоромовка): ${transcripts.getOrNull(1) ?: "Немає запису"}
            Завдання 3 (Емоційність): ${transcripts.getOrNull(2) ?: "Немає запису"}
            Завдання 4 (Вільна мова): ${transcripts.getOrNull(3) ?: "Немає запису"}

            Надай детальний аналіз у JSON форматі:
            {
                "overallScore": 0-100,
                "readingScore": 0-100,
                "dictionScore": 0-100,
                "emotionalScore": 0-100,
                "freeSpeechScore": 0-100,
                "readingFeedback": {"strengths": [...], "improvements": [...]},
                "dictionFeedback": {"strengths": [...], "improvements": [...]},
                "emotionalFeedback": {"strengths": [...], "improvements": [...]},
                "freeSpeechFeedback": {"strengths": [...], "improvements": [...]},
                "recommendations": [...],
                "strengths": [...],
                "areasToImprove": [...]
            }
        """.trimIndent()
    }

    private fun parseDiagnosticResponse(response: String): DiagnosticResult {
        // TODO: Implement proper JSON parsing with kotlinx.serialization or Gson
        // For now, return mock data
        return DiagnosticResult.mock()
    }
}
