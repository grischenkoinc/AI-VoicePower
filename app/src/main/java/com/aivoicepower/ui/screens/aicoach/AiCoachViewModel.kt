package com.aivoicepower.ui.screens.aicoach

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.audio.SoundEffect
import com.aivoicepower.audio.SoundManager
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.chat.MessageRole
import com.aivoicepower.data.content.SimulationScenariosProvider
import com.aivoicepower.data.remote.AiPrompts
import com.aivoicepower.domain.repository.AiCoachRepository
import com.aivoicepower.utils.AnalyticsTracker
import com.aivoicepower.utils.CloudTtsManager
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
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val soundManager: SoundManager,
    val ttsManager: CloudTtsManager,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _state = MutableStateFlow(AiCoachState())
    val state: StateFlow<AiCoachState> = _state.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var accumulatedVoiceText = ""
    private var voiceSilenceJob: kotlinx.coroutines.Job? = null
    private var stoppingVoice = false
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    init {
        ttsManager.warmUp()
        ttsManager.setTtsContext("coach")
        loadMessages()
        loadQuickActions()
        loadTemplates()
        observePreferences()
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
        ttsManager.stop()
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
                        quickActions = AiPrompts.DEFAULT_QUICK_ACTIONS,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadTemplates() {
        val templates = listOf(
            ConversationTemplate(
                id = "diction_tips",
                title = "Покращення дикції",
                emoji = "\uD83D\uDDE3\uFE0F",
                initialMessage = "Дай поради, як покращити дикцію. Які вправи допоможуть говорити чіткіше?"
            ),
            ConversationTemplate(
                id = "last_recording",
                title = "Аналіз останнього запису",
                emoji = "\uD83C\uDFA4",
                initialMessage = "Проаналізуй мій останній запис. На що мені варто звернути увагу?"
            ),
            ConversationTemplate(
                id = "voice_training",
                title = "Робота з голосом",
                emoji = "\uD83C\uDFB5",
                initialMessage = "Дай поради щодо роботи з голосом: постановка, тембр, гучність. Які вправи допоможуть зробити голос більш виразним?"
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

        viewModelScope.launch {
            // Fresh limit check from DB (not stale state)
            val canSend = aiCoachRepository.canSendMessage()
            if (!canSend) {
                updateRemainingMessages()
                analyticsTracker.logLimitReached("coach_messages", _state.value.isPremium)
                return@launch
            }

            analyticsTracker.logCoachMessageSent(_state.value.isPremium)
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
                    onSuccess = { aiMessage ->
                        // Update remaining messages count
                        updateRemainingMessages()

                        _state.update { it.copy(isSending = false) }
                        // soundManager.play(SoundEffect.COACH_PING) // Disabled for now
                        ttsManager.speak(aiMessage.content)

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

    private fun updateRemainingMessages() {
        viewModelScope.launch {
            val prefs = userPreferencesDataStore.userPreferencesFlow.first()
            val todayCount = aiCoachRepository.getTodayMessagesCount()
            val remaining = if (prefs.isPremium) Int.MAX_VALUE else (10 - todayCount).coerceAtLeast(0)
            _state.update {
                it.copy(
                    messagesRemaining = remaining,
                    canSendMessage = prefs.isPremium || remaining > 0
                )
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

    // ===== Voice Input (continuous mode with 3-sec silence timer) =====

    private fun createRecognizerIntent() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uk-UA")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000L)
    }

    // Mute all streams where recognizer beep might play
    private fun muteRecognizerBeep() {
        try {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)
        } catch (_: Exception) {}
    }

    private fun unmuteRecognizerBeep() {
        try {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0)
        } catch (_: Exception) {}
    }

    // Play sound through ALARM stream — unaffected by muting other streams
    private fun playCoachSound(resId: Int, volume: Float = 0.5f) {
        try {
            val mp = MediaPlayer()
            val afd = context.resources.openRawResourceFd(resId) ?: return
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            mp.setVolume(volume, volume)
            mp.setOnCompletionListener { it.release() }
            mp.prepare()
            mp.start()
        } catch (_: Exception) {}
    }

    private fun startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.update { it.copy(error = "Розпізнавання голосу недоступне") }
            return
        }

        stoppingVoice = false
        accumulatedVoiceText = ""
        voiceSilenceJob?.cancel()
        _state.update { it.copy(inputText = "") }
        muteRecognizerBeep()
        playCoachSound(com.aivoicepower.R.raw.sound_record_start, 0.4f)

        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""

                if (text.isNotBlank()) {
                    accumulatedVoiceText = if (accumulatedVoiceText.isBlank()) text
                        else "$accumulatedVoiceText $text"
                    _state.update { it.copy(inputText = accumulatedVoiceText) }
                }

                // If we're intentionally stopping — send accumulated text, don't restart
                if (stoppingVoice) {
                    playCoachSound(com.aivoicepower.R.raw.sound_record_stop, 0.5f)
                    unmuteRecognizerBeep()
                    _state.update { it.copy(isListening = false, audioLevel = 0f) }
                    if (accumulatedVoiceText.isNotBlank()) {
                        _state.update { it.copy(inputText = accumulatedVoiceText) }
                        sendMessage()
                        accumulatedVoiceText = ""
                    }
                    return
                }

                // Cancel previous silence timer
                voiceSilenceJob?.cancel()

                // Mute before restart to suppress beep
                muteRecognizerBeep()

                // Restart recognizer for continuous listening
                try {
                    _state.update { it.copy(isListening = true) }
                    speechRecognizer?.startListening(createRecognizerIntent())
                } catch (_: Exception) { }

                // Start 3-sec silence timer — if no new results, auto-send
                voiceSilenceJob = viewModelScope.launch {
                    kotlinx.coroutines.delay(3000)
                    stoppingVoice = true
                    muteRecognizerBeep()
                    speechRecognizer?.stopListening()
                    // onResults() will fire with stoppingVoice=true → play RECORD_STOP + unmute + send
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull() ?: ""
                if (partial.isNotBlank()) {
                    val display = if (accumulatedVoiceText.isBlank()) partial
                        else "$accumulatedVoiceText $partial"
                    _state.update { it.copy(inputText = display) }
                }
            }

            override fun onError(error: Int) {
                // If stopping and got error instead of results — send what we have
                if (stoppingVoice) {
                    playCoachSound(com.aivoicepower.R.raw.sound_record_stop, 0.5f)
                    unmuteRecognizerBeep()
                    _state.update { it.copy(isListening = false, audioLevel = 0f) }
                    if (accumulatedVoiceText.isNotBlank()) {
                        _state.update { it.copy(inputText = accumulatedVoiceText) }
                        sendMessage()
                        accumulatedVoiceText = ""
                    }
                    return
                }

                // During continuous mode: silence errors are expected after restart
                if (error == SpeechRecognizer.ERROR_NO_MATCH ||
                    error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    if (accumulatedVoiceText.isNotBlank()) return
                    unmuteRecognizerBeep()
                    _state.update { it.copy(isListening = false, audioLevel = 0f) }
                    return
                }

                if (error == SpeechRecognizer.ERROR_CLIENT) return // Ignore client errors on restart

                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Помилка аудіо"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Немає дозволу на мікрофон"
                    SpeechRecognizer.ERROR_NETWORK -> "Помилка мережі"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Таймаут мережі"
                    else -> "Помилка розпізнавання"
                }
                voiceSilenceJob?.cancel()
                unmuteRecognizerBeep()
                _state.update {
                    it.copy(
                        isListening = false,
                        audioLevel = 0f,
                        error = errorMessage
                    )
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {
                _state.update { it.copy(isListening = true) }
            }

            override fun onEndOfSpeech() {
                // Don't set isListening=false — we'll restart in onResults
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                _state.update { it.copy(audioLevel = normalized) }
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(createRecognizerIntent())
    }

    private fun stopVoiceInput() {
        stoppingVoice = true
        voiceSilenceJob?.cancel()
        muteRecognizerBeep()
        // stopListening() triggers onResults() with final recognized text
        // onResults() sees stoppingVoice=true → plays RECORD_STOP via ALARM stream → unmutes → sends
        speechRecognizer?.stopListening()
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
                    appendLine("Diqto — Розмова з AI Тренером")
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
                    appendLine("Експортовано з Diqto")
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
                    onSuccess = { aiMessage ->
                        _state.update { it.copy(isSending = false) }
                        ttsManager.speak(aiMessage.content)
                        loadQuickActions()
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

}
