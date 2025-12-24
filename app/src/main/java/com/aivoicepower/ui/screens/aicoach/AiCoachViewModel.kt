package com.aivoicepower.ui.screens.aicoach

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.chat.MessageRole
import com.aivoicepower.data.content.SimulationScenariosProvider
import com.aivoicepower.domain.repository.AiCoachRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AiCoachViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val aiCoachRepository: AiCoachRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(AiCoachState())
    val state: StateFlow<AiCoachState> = _state.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    init {
        loadMessages()
        loadQuickActions()
        loadTemplates()
        observePreferences()
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            aiCoachRepository.getMessagesFlow().collect { messages ->
                _state.update { it.copy(messages = messages) }
            }
        }
    }

    private fun loadQuickActions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val actions = aiCoachRepository.getQuickActions()
                _state.update {
                    it.copy(
                        quickActions = actions,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        quickActions = getDefaultQuickActions(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadTemplates() {
        val templates = listOf(
            ConversationTemplate(
                id = "interview_prep",
                title = "Підготовка до співбесіди",
                emoji = "\uD83D\uDCBC",
                initialMessage = "Допоможи мені підготуватися до співбесіди. Я хочу відпрацювати типові питання та покращити свої відповіді."
            ),
            ConversationTemplate(
                id = "last_recording",
                title = "Аналіз останнього запису",
                emoji = "\uD83C\uDFA4",
                initialMessage = "Проаналізуй мій останній запис. На що мені варто звернути увагу?"
            ),
            ConversationTemplate(
                id = "presentation_sim",
                title = "Симуляція презентації",
                emoji = "\uD83D\uDCCA",
                initialMessage = "Хочу відпрацювати презентацію. Задавай мені складні питання та давай feedback."
            ),
            ConversationTemplate(
                id = "confidence_tips",
                title = "Поради для впевненості",
                emoji = "\uD83D\uDCAA",
                initialMessage = "Дай поради як говорити більш впевнено та переконливо."
            ),
            ConversationTemplate(
                id = "weekly_plan",
                title = "План на тиждень",
                emoji = "\uD83D\uDCC5",
                initialMessage = "Склади для мене персональний план вправ на тиждень на основі моїх слабких місць."
            )
        )

        _state.update { it.copy(templates = templates) }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userPreferencesDataStore.userPreferencesFlow.collect { prefs ->
                val todayCount = aiCoachRepository.getTodayMessagesCount()
                val remaining = if (prefs.isPremium) {
                    Int.MAX_VALUE
                } else {
                    (10 - todayCount).coerceAtLeast(0)
                }

                _state.update {
                    it.copy(
                        isPremium = prefs.isPremium,
                        messagesRemaining = remaining,
                        canSendMessage = prefs.isPremium || remaining > 0
                    )
                }
            }
        }
    }

    fun onEvent(event: AiCoachEvent) {
        when (event) {
            is AiCoachEvent.InputChanged -> {
                _state.update { it.copy(inputText = event.text) }
            }

            AiCoachEvent.SendMessageClicked -> {
                sendMessage()
            }

            is AiCoachEvent.QuickActionClicked -> {
                _state.update { it.copy(inputText = event.action) }
            }

            AiCoachEvent.ClearConversationClicked -> {
                clearConversation()
            }

            AiCoachEvent.ErrorDismissed -> {
                _state.update { it.copy(error = null) }
            }

            // Voice Input
            AiCoachEvent.StartVoiceInput -> startVoiceInput()
            AiCoachEvent.StopVoiceInput -> stopVoiceInput()
            is AiCoachEvent.VoiceInputTranscribed -> {
                _state.update { it.copy(inputText = event.text, isListening = false) }
            }

            // Audio Upload
            AiCoachEvent.UploadAudioClicked -> {
                // Handled by UI (file picker)
            }
            is AiCoachEvent.AudioFileSelected -> {
                handleAudioFileSelected(event.uri)
            }

            // Simulations
            AiCoachEvent.ShowScenarioDialog -> {
                _state.update { it.copy(showScenarioDialog = true) }
            }
            AiCoachEvent.HideScenarioDialog -> {
                _state.update { it.copy(showScenarioDialog = false) }
            }
            is AiCoachEvent.StartSimulation -> {
                startSimulation(event.scenario)
            }
            AiCoachEvent.NextSimulationStep -> {
                nextSimulationStep()
            }
            AiCoachEvent.ExitSimulation -> {
                exitSimulation()
            }

            // Export
            AiCoachEvent.ExportConversation -> {
                exportConversation()
            }

            // Templates
            is AiCoachEvent.ApplyTemplate -> {
                applyTemplate(event.template)
            }
        }
    }

    private fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isBlank()) return
        if (!_state.value.canSendMessage) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSending = true,
                    inputText = "",
                    error = null
                )
            }

            try {
                val result = aiCoachRepository.sendMessage(text)

                result.fold(
                    onSuccess = {
                        _state.update { it.copy(isSending = false) }

                        // Check if in simulation mode
                        if (_state.value.activeSimulation != null) {
                            nextSimulationStep()
                        } else {
                            loadQuickActions()
                        }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isSending = false,
                                error = error.message ?: "Помилка надсилання повідомлення"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSending = false,
                        error = e.message ?: "Невідома помилка"
                    )
                }
            }
        }
    }

    private fun clearConversation() {
        viewModelScope.launch {
            try {
                aiCoachRepository.clearConversation()
                loadQuickActions()
                _state.update {
                    it.copy(
                        activeSimulation = null,
                        simulationStep = 0
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Помилка очищення: ${e.message}")
                }
            }
        }
    }

    // ===== Voice Input =====

    private fun startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.update { it.copy(error = "Розпізнавання голосу недоступне") }
            return
        }

        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uk-UA")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                _state.update { it.copy(inputText = text, isListening = false) }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                _state.update { it.copy(inputText = text) }
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Помилка аудіо"
                    SpeechRecognizer.ERROR_CLIENT -> "Помилка клієнта"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Немає дозволу на мікрофон"
                    SpeechRecognizer.ERROR_NETWORK -> "Помилка мережі"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Таймаут мережі"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Не вдалося розпізнати"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Не почуто мовлення"
                    else -> "Помилка розпізнавання"
                }
                _state.update {
                    it.copy(
                        isListening = false,
                        error = errorMessage
                    )
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {
                _state.update { it.copy(isListening = true) }
            }

            override fun onEndOfSpeech() {
                _state.update { it.copy(isListening = false) }
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    private fun stopVoiceInput() {
        speechRecognizer?.stopListening()
        _state.update { it.copy(isListening = false) }
    }

    // ===== Audio Upload =====

    private fun handleAudioFileSelected(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingAudio = true) }

            try {
                // For now, send a message indicating audio was uploaded
                // In a real implementation, you would transcribe the audio first
                val analysisPrompt = """
Користувач завантажив аудіо запис для аналізу.

Оскільки я не можу безпосередньо прослухати аудіо, дай загальні поради щодо:
1. Як покращити дикцію
2. Як працювати над темпом мовлення
3. Як додати виразності інтонації
4. Як уникати слів-паразитів

Порадь також, на що звертати увагу під час самостійного аналізу запису.
                """.trimIndent()

                val result = aiCoachRepository.sendMessage(analysisPrompt)

                result.fold(
                    onSuccess = {
                        _state.update { it.copy(isUploadingAudio = false) }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isUploadingAudio = false,
                                error = "Помилка аналізу: ${error.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isUploadingAudio = false,
                        error = "Помилка завантаження: ${e.message}"
                    )
                }
            }
        }
    }

    // ===== Simulations =====

    fun getSimulationScenarios(): List<SimulationScenario> {
        return SimulationScenariosProvider.getAllScenarios()
    }

    private fun startSimulation(scenario: SimulationScenario) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    activeSimulation = scenario,
                    simulationStep = 0,
                    showScenarioDialog = false
                )
            }

            // Send introduction message
            val introMessage = """
Розпочинаємо симуляцію: "${scenario.title}"

${scenario.description}

Кількість кроків: ${scenario.steps.size}

Я буду задавати питання, а ти відповідай як у реальній ситуації. Після кожної відповіді отримаєш підказку для наступного кроку.

Почнемо!
            """.trimIndent()

            aiCoachRepository.sendMessage(introMessage)

            // Send first step
            val firstStep = scenario.steps.firstOrNull()
            if (firstStep != null) {
                kotlinx.coroutines.delay(1000)
                aiCoachRepository.sendMessage("**Крок 1/${scenario.steps.size}**\n\n${firstStep.aiPrompt}\n\n_Підказка: ${firstStep.userHint}_")
            }
        }
    }

    private fun nextSimulationStep() {
        val currentStep = _state.value.simulationStep
        val scenario = _state.value.activeSimulation ?: return

        val nextStep = currentStep + 1

        if (nextStep < scenario.steps.size) {
            viewModelScope.launch {
                _state.update { it.copy(simulationStep = nextStep) }

                val step = scenario.steps[nextStep]
                aiCoachRepository.sendMessage("**Крок ${nextStep + 1}/${scenario.steps.size}**\n\n${step.aiPrompt}\n\n_Підказка: ${step.userHint}_")
            }
        } else {
            // Simulation complete
            viewModelScope.launch {
                val feedbackPrompt = """
Симуляція "${scenario.title}" завершена!

Дай короткий підсумок на основі відповідей користувача:
1. **Сильні сторони** — що було добре
2. **Що покращити** — конкретні рекомендації
3. **Загальна оцінка** — від 1 до 10

Будь конструктивним та мотивуючим!
                """.trimIndent()

                aiCoachRepository.sendMessage(feedbackPrompt)

                _state.update {
                    it.copy(
                        activeSimulation = null,
                        simulationStep = 0
                    )
                }
            }
        }
    }

    private fun exitSimulation() {
        viewModelScope.launch {
            aiCoachRepository.sendMessage("Симуляцію завершено достроково. Можемо продовжити звичайну розмову або почати нову симуляцію.")

            _state.update {
                it.copy(
                    activeSimulation = null,
                    simulationStep = 0
                )
            }
        }
    }

    // ===== Export =====

    private fun exportConversation() {
        viewModelScope.launch {
            try {
                val messages = _state.value.messages

                if (messages.isEmpty()) {
                    _state.update { it.copy(error = "Немає повідомлень для експорту") }
                    return@launch
                }

                val textContent = buildString {
                    appendLine("AI VoicePower — Розмова з AI Тренером")
                    appendLine("Дата: ${getCurrentDateTimeString()}")
                    appendLine()
                    appendLine("=" .repeat(50))
                    appendLine()

                    messages.forEach { message ->
                        when (message.role) {
                            MessageRole.USER -> {
                                appendLine("Користувач:")
                                appendLine(message.content)
                            }
                            MessageRole.ASSISTANT -> {
                                appendLine("AI Тренер:")
                                appendLine(message.content)
                            }
                            MessageRole.SYSTEM -> {
                                // Skip system messages
                            }
                        }
                        appendLine()
                    }

                    appendLine("=" .repeat(50))
                    appendLine()
                    appendLine("Експортовано з AI VoicePower")
                }

                // Save to file
                val fileName = "ai_coach_${System.currentTimeMillis()}.txt"
                val file = File(context.getExternalFilesDir(null), fileName)
                file.writeText(textContent)

                // Share
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, textContent)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                context.startActivity(Intent.createChooser(shareIntent, "Поділитися розмовою").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })

            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Помилка експорту: ${e.message}")
                }
            }
        }
    }

    // ===== Templates =====

    private fun applyTemplate(template: ConversationTemplate) {
        viewModelScope.launch {
            _state.update { it.copy(isSending = true) }

            try {
                val result = aiCoachRepository.sendMessage(template.initialMessage)

                result.fold(
                    onSuccess = {
                        _state.update { it.copy(isSending = false) }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isSending = false,
                                error = error.message ?: "Помилка"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSending = false,
                        error = e.message ?: "Невідома помилка"
                    )
                }
            }
        }
    }

    private fun getCurrentDateTimeString(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("uk"))
        return sdf.format(Date())
    }

    private fun getDefaultQuickActions(): List<String> {
        return listOf(
            "Дай поради для покращення мовлення",
            "Як підготуватися до виступу?",
            "Які вправи мені підходять?",
            "Як позбутися нервозності?",
            "Підготуй мене до співбесіди"
        )
    }
}
