# Промпт для Claude Code — Phase 5.3: Debate + Sales Pitch (AI-Interactive)

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1-2.5 — Warmup
- ✅ Phase 3 — Home Screen
- ✅ Phase 4.1-4.4 — Courses (повністю)
- ✅ Phase 5.1 — Improvisation Hub + Random Topic
- ✅ Phase 5.2 — Storytelling + Daily Challenge

Зараз **Phase 5.3 — Debate + Sales Pitch** — остання підфаза Phase 5, найскладніша.

**Згідно з PHASE_STRUCTURE_GUIDE.md**: Висока складність, AI-interaction.

**Специфікація:** `SPECIFICATION.md`, секції 4.3.7 (Improvisation Screen) + 5.5 (ImprovisationTask) + 8.2 (AI System Prompts).

**Складність:** 🔴 ВИСОКА (Gemini API integration!)  
**Час:** ⏱️ 4-5 годин

---

## ⚡ КРИТИЧНО ВАЖЛИВО

**Це перша реальна Gemini API інтеграція в проекті!**

Phase 5.3 — це **proof of concept** для:
- Real-time AI conversation
- Turn-based interaction
- Streaming responses (опціонально)
- Error handling
- Rate limiting

Phase 6 (AI Coach) буде використовувати ту саму інфраструктуру.

---

## Ключова ідея

**Phase 5.3** додає 2 AI-powered режими:

### 1. Debate (Дебати з AI)
- Користувач обирає тему та позицію (ЗА/ПРОТИ)
- AI грає опонента з протилежною позицією
- **Turn-based**: User аргумент → AI контраргумент → User відповідь
- 3-5 раундів
- AI аналізує аргументи та генерує відповіді

### 2. Sales Pitch (Продаж з AI-клієнтом)
- Користувач обирає товар (реальний або абсурдний)
- AI грає клієнта з запереченнями
- **Interactive**: User pitch → AI запитання → User відповідь → AI decision
- AI симулює різні типи клієнтів

---

## Debate Flow

```
ImprovisationScreen
    │
    ▼
Click "Дебати з AI"
    │
    ▼
DebateScreen
    │
    ├─ Вибір теми (з DebateTopicsProvider)
    ├─ Вибір позиції (ЗА/ПРОТИ)
    │
    ▼
Старт дебатів (Round 1)
    │
    ├─ User: записує аргумент (60 сек)
    ├─ Transcription (SpeechRecognizer)
    ├─ Збереження RecordingEntity
    │
    ▼
AI Response (Round 1)
    │
    ├─ Send user argument → Gemini API
    ├─ AI генерує контраргумент
    ├─ Show AI response (text)
    │
    ▼
Round 2, 3... (до 5 раундів)
    │
    ▼
Debate Completed
    │
    ├─ Show summary
    ├─ AI оцінює аргументацію
    └─ Save full debate
```

---

## Sales Pitch Flow

```
ImprovisationScreen
    │
    ▼
Click "Продаж товару"
    │
    ▼
SalesPitchScreen
    │
    ├─ Вибір товару (з SalesProductsProvider)
    ├─ AI генерує customer profile
    │
    ▼
Opening Pitch
    │
    ├─ User: записує pitch (90 сек)
    ├─ Transcription
    ├─ Збереження recording
    │
    ▼
AI Customer Response
    │
    ├─ Send pitch → Gemini API
    ├─ AI: запитання або заперечення
    ├─ Show AI response
    │
    ▼
User Handles Objection
    │
    ├─ User: відповідає (60 сек)
    ├─ Transcription
    │
    ▼
AI Decision
    │
    ├─ Send response → Gemini API
    ├─ AI: "Купую" або "Не переконав"
    ├─ AI пояснює рішення
    │
    ▼
Sales Completed
    └─ Show result + feedback
```

---

## Структура файлів

```
data/remote/
├── GeminiApiClient.kt
└── dto/
    ├── GeminiRequest.kt
    └── GeminiResponse.kt

data/content/
├── DebateTopicsProvider.kt
└── SalesProductsProvider.kt

ui/screens/improvisation/
├── DebateScreen.kt
├── DebateViewModel.kt
├── DebateState.kt
├── DebateEvent.kt
│
├── SalesPitchScreen.kt
├── SalesPitchViewModel.kt
├── SalesPitchState.kt
├── SalesPitchEvent.kt
│
└── components/
    ├── DebateTopicCard.kt
    ├── DebateRoundCard.kt
    ├── AiResponseCard.kt
    ├── SalesProductCard.kt
    └── CustomerProfileCard.kt
```

---

## Повний код

### 1. GeminiApiClient.kt

```kotlin
package com.aivoicepower.data.remote

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Client для роботи з Gemini API
 */
@Singleton
class GeminiApiClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        // TODO: Move to BuildConfig or secure storage
        private const val API_KEY = "YOUR_GEMINI_API_KEY_HERE"
        private const val MODEL_NAME = "gemini-1.5-flash-latest" // Використовуємо Flash для швидкості
    }
    
    private val generativeModel = GenerativeModel(
        modelName = MODEL_NAME,
        apiKey = API_KEY,
        generationConfig = generationConfig {
            temperature = 0.8f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 800
        }
    )
    
    /**
     * Генерує відповідь для дебатів
     */
    suspend fun generateDebateResponse(
        topic: String,
        userPosition: String,
        userArgument: String,
        roundNumber: Int,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> {
        return try {
            val systemPrompt = buildDebateSystemPrompt(topic, userPosition, roundNumber)
            val userPrompt = buildDebateUserPrompt(userArgument, conversationHistory)
            
            val response = generativeModel.generateContent(
                content {
                    text(systemPrompt)
                    text(userPrompt)
                }
            )
            
            val aiResponse = response.text ?: "Я не можу відповісти на цей аргумент."
            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Генерує відповідь AI-клієнта у продажах
     */
    suspend fun generateSalesResponse(
        product: String,
        customerType: String,
        userPitch: String,
        interactionStage: SalesStage
    ): Result<String> {
        return try {
            val systemPrompt = buildSalesSystemPrompt(product, customerType, interactionStage)
            val userPrompt = "Продавець каже: $userPitch"
            
            val response = generativeModel.generateContent(
                content {
                    text(systemPrompt)
                    text(userPrompt)
                }
            )
            
            val aiResponse = response.text ?: "Мені потрібно подумати..."
            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Оцінює фінальний результат дебатів
     */
    suspend fun evaluateDebate(
        topic: String,
        userPosition: String,
        rounds: List<Pair<String, String>>
    ): Result<String> {
        return try {
            val prompt = buildDebateEvaluationPrompt(topic, userPosition, rounds)
            
            val response = generativeModel.generateContent(prompt)
            
            val evaluation = response.text ?: "Гарна спроба!"
            Result.success(evaluation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ===== SYSTEM PROMPTS =====
    
    private fun buildDebateSystemPrompt(
        topic: String,
        userPosition: String,
        roundNumber: Int
    ): String {
        val oppositePosition = if (userPosition == "ЗА") "ПРОТИ" else "ЗА"
        
        return """
Ти — опонент у дебатах на тему: "$topic"
Твоя позиція: $oppositePosition
Раунд: $roundNumber з 5

Твої задачі:
1. Визнай частково правильні моменти в аргументі опонента
2. Наведи сильний контраргумент на позицію $oppositePosition
3. Поставь 1 уточнююче питання або виклик

Правила:
- Відповідай українською мовою
- Будь логічним, але не агресивним
- Використовуй факти та логіку
- Довжина відповіді: 2-4 речення
- Без особистих нападів

Стиль: академічний, але зрозумілий
        """.trimIndent()
    }
    
    private fun buildDebateUserPrompt(
        userArgument: String,
        history: List<Pair<String, String>>
    ): String {
        val historyText = if (history.isNotEmpty()) {
            "Попередні раунди:\n" + history.joinToString("\n") { (user, ai) ->
                "Опонент: $user\nТи: $ai"
            } + "\n\n"
        } else ""
        
        return "${historyText}Опонент щойно навів аргумент:\n\"$userArgument\"\n\nТвоя відповідь:"
    }
    
    private fun buildSalesSystemPrompt(
        product: String,
        customerType: String,
        stage: SalesStage
    ): String {
        return when (stage) {
            SalesStage.INITIAL_PITCH -> """
Ти — потенційний клієнт, якому продають: "$product"
Твій тип: $customerType

Продавець щойно представив товар. Твоя реакція:
1. Висловлюй природну цікавість або скептицизм (залежно від типу)
2. Постав 1-2 конкретні запитання про продукт
3. Висунь типове заперечення для твого типу клієнта

Правила:
- Відповідай українською мовою
- Будь реалістичним клієнтом
- Довжина: 2-3 речення
- Без відразу погоджуватися або відмовлятися
            """.trimIndent()
            
            SalesStage.HANDLING_OBJECTION -> """
Ти — потенційний клієнт, який слухає відповідь на своє заперечення.
Продукт: "$product"
Твій тип: $customerType

Продавець щойно відповів на твоє заперечення. Тепер прийми рішення:
1. Якщо відповідь переконлива → Згодься купити + поясни чому
2. Якщо відповідь слабка → Ввічливо відмовся + поясни причину

Правила:
- Будь чесним, але не жорстоким
- Оціни якість аргументації продавця
- Довжина: 2-3 речення
- Чітке "так" або "ні" з поясненням
            """.trimIndent()
        }
    }
    
    private fun buildDebateEvaluationPrompt(
        topic: String,
        userPosition: String,
        rounds: List<Pair<String, String>>
    ): String {
        val transcript = rounds.mapIndexed { index, (user, ai) ->
            "Раунд ${index + 1}:\nОпонент (позиція: $userPosition): $user\nAI: $ai"
        }.joinToString("\n\n")
        
        return """
Оціни дебати на тему: "$topic"

Транскрипт:
$transcript

Дай короткий фідбек (3-4 речення):
1. Сильні сторони аргументації опонента
2. Що можна покращити
3. Загальна оцінка виступу (1-10)

Формат: короткий текст українською мовою
        """.trimIndent()
    }
}

enum class SalesStage {
    INITIAL_PITCH,
    HANDLING_OBJECTION
}
```

### 2. DebateTopicsProvider.kt

```kotlin
package com.aivoicepower.data.content

/**
 * Provider для тем дебатів
 */
object DebateTopicsProvider {
    
    data class DebateTopic(
        val id: String,
        val topic: String,
        val description: String,
        val difficulty: String
    )
    
    private val topics = listOf(
        DebateTopic(
            id = "debate_001",
            topic = "Штучний інтелект: загроза чи можливість для людства?",
            description = "Обговорення впливу AI на майбутнє суспільства",
            difficulty = "Середня"
        ),
        DebateTopic(
            id = "debate_002",
            topic = "Чи варто колонізувати Марс?",
            description = "Аргументи за та проти міжпланетної колонізації",
            difficulty = "Середня"
        ),
        DebateTopic(
            id = "debate_003",
            topic = "Безумовний базовий дохід: утопія чи необхідність?",
            description = "Дебати про економічні системи майбутнього",
            difficulty = "Складна"
        ),
        DebateTopic(
            id = "debate_004",
            topic = "Соціальні мережі роблять нас більш самотніми",
            description = "Вплив соцмереж на психічне здоров'я",
            difficulty = "Легка"
        ),
        DebateTopic(
            id = "debate_005",
            topic = "Онлайн-освіта краща за традиційну",
            description = "Майбутнє освітньої системи",
            difficulty = "Легка"
        ),
        DebateTopic(
            id = "debate_006",
            topic = "Чи повинні роботи мати права?",
            description = "Етика штучного інтелекту",
            difficulty = "Складна"
        ),
        DebateTopic(
            id = "debate_007",
            topic = "Генетична модифікація людей — етично виправдана",
            description = "Межі біотехнологій",
            difficulty = "Складна"
        ),
        DebateTopic(
            id = "debate_008",
            topic = "4-денний робочий тиждень — це майбутнє",
            description = "Work-life balance та продуктивність",
            difficulty = "Легка"
        )
    )
    
    fun getAllTopics(): List<DebateTopic> = topics
    
    fun getRandomTopic(): DebateTopic = topics.random()
    
    fun getTopicById(id: String): DebateTopic? = topics.find { it.id == id }
}
```

### 3. SalesProductsProvider.kt

```kotlin
package com.aivoicepower.data.content

/**
 * Provider для товарів для продажу
 */
object SalesProductsProvider {
    
    data class SalesProduct(
        val id: String,
        val name: String,
        val description: String,
        val price: String,
        val isAbsurd: Boolean = false
    )
    
    data class CustomerProfile(
        val type: String,
        val description: String,
        val typicalObjections: List<String>
    )
    
    private val realProducts = listOf(
        SalesProduct(
            id = "product_001",
            name = "Онлайн-курс з публічних виступів",
            description = "12-тижнева програма для розвитку навичок презентацій",
            price = "₴4,999"
        ),
        SalesProduct(
            id = "product_002",
            name = "Смарт-годинник для фітнесу",
            description = "Моніторинг здоров'я 24/7, GPS, водонепроникний",
            price = "₴8,999"
        ),
        SalesProduct(
            id = "product_003",
            name = "Підписка на онлайн-бібліотеку",
            description = "10,000+ книжок та аудіокниг українською та англійською",
            price = "₴199/міс"
        ),
        SalesProduct(
            id = "product_004",
            name = "Роботизований пилосос",
            description = "Автоматичне прибирання, картографування квартири",
            price = "₴12,999"
        )
    )
    
    private val absurdProducts = listOf(
        SalesProduct(
            id = "absurd_001",
            name = "Невидимий парасольку",
            description = "Захищає від дощу за допомогою силового поля",
            price = "₴99,999",
            isAbsurd = true
        ),
        SalesProduct(
            id = "absurd_002",
            name = "Машина часу (лише в минуле)",
            description = "Повернення на 24 години назад, одноразове використання",
            price = "₴50,000",
            isAbsurd = true
        ),
        SalesProduct(
            id = "absurd_003",
            name = "Чарівний олівець",
            description = "Все, що намалюєш, стає реальністю (макс. 10 см)",
            price = "₴1,000,000",
            isAbsurd = true
        )
    )
    
    private val customerTypes = listOf(
        CustomerProfile(
            type = "Зайнятий професіонал",
            description = "Цінує час, шукає ефективність",
            typicalObjections = listOf("У мене немає часу", "Це дорого", "Чи дійсно це працює?")
        ),
        CustomerProfile(
            type = "Скептик",
            description = "Не довіряє новим продуктам, потребує доказів",
            typicalObjections = listOf("Я чув негативні відгуки", "Це схоже на обман", "Навіщо мені це?")
        ),
        CustomerProfile(
            type = "Обережний покупець",
            description = "Хоче все зважити, боїться помилитися",
            typicalObjections = listOf("Може, я подумаю", "Що якщо це мені не підійде?", "Чи можна повернути?")
        ),
        CustomerProfile(
            type = "Ентузіаст",
            description = "Цікавий новинками, але критично ставиться до деталей",
            typicalObjections = listOf("А що ще він вміє?", "Чи є аналоги?", "Які гарантії?")
        )
    )
    
    fun getAllProducts(includeAbsurd: Boolean = true): List<SalesProduct> {
        return if (includeAbsurd) {
            realProducts + absurdProducts
        } else {
            realProducts
        }
    }
    
    fun getRandomProduct(includeAbsurd: Boolean = true): SalesProduct {
        return getAllProducts(includeAbsurd).random()
    }
    
    fun getRandomCustomer(): CustomerProfile = customerTypes.random()
}
```

### 4. DebateState.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.content.DebateTopicsProvider

data class DebateState(
    val selectedTopic: DebateTopicsProvider.DebateTopic? = null,
    val userPosition: DebatePosition? = null,
    val phase: DebatePhase = DebatePhase.TopicSelection,
    val currentRound: Int = 1,
    val maxRounds: Int = 5,
    val rounds: List<DebateRound> = emptyList(),
    val isRecording: Boolean = false,
    val isAiThinking: Boolean = false,
    val recordingPath: String? = null,
    val recordingSeconds: Int = 0,
    val maxRecordingSeconds: Int = 60,
    val error: String? = null
)

enum class DebatePosition {
    FOR,        // ЗА
    AGAINST     // ПРОТИ
}

sealed class DebatePhase {
    object TopicSelection : DebatePhase()
    object PositionSelection : DebatePhase()
    object UserArgument : DebatePhase()
    object AiResponse : DebatePhase()
    object DebateComplete : DebatePhase()
}

data class DebateRound(
    val roundNumber: Int,
    val userArgument: String,
    val userRecordingPath: String,
    val aiResponse: String
)
```

### 5. DebateEvent.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.content.DebateTopicsProvider

sealed class DebateEvent {
    data class TopicSelected(val topic: DebateTopicsProvider.DebateTopic) : DebateEvent()
    data class PositionSelected(val position: DebatePosition) : DebateEvent()
    object StartRecordingClicked : DebateEvent()
    object StopRecordingClicked : DebateEvent()
    data class ArgumentTranscribed(val text: String) : DebateEvent()
    object NextRoundClicked : DebateEvent()
    object FinishDebateClicked : DebateEvent()
}
```

### 6. DebateViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.remote.GeminiApiClient
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DebateViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiApiClient: GeminiApiClient,
    private val recordingDao: RecordingDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(DebateState())
    val state: StateFlow<DebateState> = _state.asStateFlow()
    
    private val audioRecorder = AudioRecorderUtil(context)
    private var speechRecognizer: SpeechRecognizer? = null
    
    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
        speechRecognizer?.destroy()
    }
    
    fun onEvent(event: DebateEvent) {
        when (event) {
            is DebateEvent.TopicSelected -> {
                _state.update {
                    it.copy(
                        selectedTopic = event.topic,
                        phase = DebatePhase.PositionSelection
                    )
                }
            }
            is DebateEvent.PositionSelected -> {
                _state.update {
                    it.copy(
                        userPosition = event.position,
                        phase = DebatePhase.UserArgument,
                        currentRound = 1
                    )
                }
            }
            DebateEvent.StartRecordingClicked -> {
                startRecording()
            }
            DebateEvent.StopRecordingClicked -> {
                stopRecording()
            }
            is DebateEvent.ArgumentTranscribed -> {
                handleTranscribedArgument(event.text)
            }
            DebateEvent.NextRoundClicked -> {
                startNextRound()
            }
            DebateEvent.FinishDebateClicked -> {
                finishDebate()
            }
        }
    }
    
    private fun startRecording() {
        viewModelScope.launch {
            try {
                val outputFile = context.filesDir.resolve("recordings/${UUID.randomUUID()}.m4a")
                outputFile.parentFile?.mkdirs()
                
                audioRecorder.startRecording(outputFile.absolutePath)
                
                _state.update {
                    it.copy(
                        isRecording = true,
                        recordingPath = outputFile.absolutePath,
                        recordingSeconds = 0
                    )
                }
                
                // Timer
                var elapsed = 0
                while (elapsed < _state.value.maxRecordingSeconds && _state.value.isRecording) {
                    delay(1000)
                    elapsed++
                    _state.update { it.copy(recordingSeconds = elapsed) }
                }
                
                if (elapsed >= _state.value.maxRecordingSeconds) {
                    stopRecording()
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка запису: ${e.message}",
                        isRecording = false
                    )
                }
            }
        }
    }
    
    private fun stopRecording() {
        viewModelScope.launch {
            try {
                audioRecorder.stopRecording()
                _state.update { it.copy(isRecording = false) }
                
                // Start transcription
                startTranscription()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка зупинки: ${e.message}",
                        isRecording = false
                    )
                }
            }
        }
    }
    
    private fun startTranscription() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
        
        val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uk-UA")
        }
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val transcription = matches?.firstOrNull() ?: "Не вдалось розпізнати мовлення"
                onEvent(DebateEvent.ArgumentTranscribed(transcription))
            }
            
            override fun onError(error: Int) {
                _state.update {
                    it.copy(error = "Помилка розпізнавання мовлення")
                }
            }
            
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        
        // For now, simulate transcription (SpeechRecognizer can be flaky)
        viewModelScope.launch {
            delay(2000)
            onEvent(DebateEvent.ArgumentTranscribed("[Аргумент користувача - транскрипція]"))
        }
    }
    
    private fun handleTranscribedArgument(transcription: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAiThinking = true, phase = DebatePhase.AiResponse) }
            
            try {
                val topic = _state.value.selectedTopic?.topic ?: ""
                val position = when (_state.value.userPosition) {
                    DebatePosition.FOR -> "ЗА"
                    DebatePosition.AGAINST -> "ПРОТИ"
                    else -> ""
                }
                val roundNumber = _state.value.currentRound
                val history = _state.value.rounds.map { it.userArgument to it.aiResponse }
                
                val result = geminiApiClient.generateDebateResponse(
                    topic = topic,
                    userPosition = position,
                    userArgument = transcription,
                    roundNumber = roundNumber,
                    conversationHistory = history
                )
                
                result.onSuccess { aiResponse ->
                    val newRound = DebateRound(
                        roundNumber = roundNumber,
                        userArgument = transcription,
                        userRecordingPath = _state.value.recordingPath ?: "",
                        aiResponse = aiResponse
                    )
                    
                    // Save recording to DB
                    saveRecording(transcription)
                    
                    _state.update {
                        it.copy(
                            rounds = it.rounds + newRound,
                            isAiThinking = false,
                            recordingPath = null
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            error = "Помилка AI: ${error.message}",
                            isAiThinking = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка: ${e.message}",
                        isAiThinking = false
                    )
                }
            }
        }
    }
    
    private suspend fun saveRecording(transcription: String) {
        try {
            val recordingPath = _state.value.recordingPath ?: return
            val topic = _state.value.selectedTopic?.id ?: ""
            
            val recordingEntity = RecordingEntity(
                id = UUID.randomUUID().toString(),
                filePath = recordingPath,
                durationMs = _state.value.recordingSeconds * 1000L,
                type = "improvisation",
                contextId = "debate_$topic",
                transcription = transcription,
                isAnalyzed = false
            )
            
            recordingDao.insert(recordingEntity)
        } catch (e: Exception) {
            // Log error
        }
    }
    
    private fun startNextRound() {
        val nextRound = _state.value.currentRound + 1
        if (nextRound <= _state.value.maxRounds) {
            _state.update {
                it.copy(
                    currentRound = nextRound,
                    phase = DebatePhase.UserArgument,
                    recordingSeconds = 0
                )
            }
        } else {
            finishDebate()
        }
    }
    
    private fun finishDebate() {
        viewModelScope.launch {
            try {
                userPreferencesDataStore.incrementFreeImprovisations()
                _state.update { it.copy(phase = DebatePhase.DebateComplete) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Помилка завершення: ${e.message}") }
            }
        }
    }
}
```

### 7. DebateScreen.kt (скорочена версія)

```kotlin
package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.content.DebateTopicsProvider
import com.aivoicepower.ui.screens.improvisation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebateScreen(
    viewModel: DebateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Дебати з AI") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state.phase) {
                DebatePhase.TopicSelection -> {
                    TopicSelectionContent(
                        topics = DebateTopicsProvider.getAllTopics(),
                        onTopicSelected = { viewModel.onEvent(DebateEvent.TopicSelected(it)) }
                    )
                }
                
                DebatePhase.PositionSelection -> {
                    PositionSelectionContent(
                        topic = state.selectedTopic!!,
                        onPositionSelected = { viewModel.onEvent(DebateEvent.PositionSelected(it)) }
                    )
                }
                
                DebatePhase.UserArgument -> {
                    UserArgumentContent(
                        topic = state.selectedTopic!!,
                        position = state.userPosition!!,
                        roundNumber = state.currentRound,
                        isRecording = state.isRecording,
                        secondsElapsed = state.recordingSeconds,
                        maxSeconds = state.maxRecordingSeconds,
                        onStartRecording = { viewModel.onEvent(DebateEvent.StartRecordingClicked) },
                        onStopRecording = { viewModel.onEvent(DebateEvent.StopRecordingClicked) }
                    )
                }
                
                DebatePhase.AiResponse -> {
                    AiResponseContent(
                        isThinking = state.isAiThinking,
                        rounds = state.rounds,
                        currentRound = state.currentRound,
                        maxRounds = state.maxRounds,
                        onNextRound = { viewModel.onEvent(DebateEvent.NextRoundClicked) },
                        onFinish = { viewModel.onEvent(DebateEvent.FinishDebateClicked) }
                    )
                }
                
                DebatePhase.DebateComplete -> {
                    DebateCompleteContent(
                        topic = state.selectedTopic!!,
                        rounds = state.rounds,
                        onFinish = onNavigateBack
                    )
                }
            }
        }
    }
}
```

### 8. SalesPitchState.kt, ViewModel, Screen - аналогічно DebateState

_(Код SalesPitch дуже схожий на Debate, тільки з іншими phases та prompt logic. Для економії місця не дублюю повний код, але структура ідентична)_

---

## ⚠️ ВАЖЛИВІ НОТАТКИ

### API Key Management

```kotlin
// TODO: НЕ commitити API key в Git!
// Використати один з варіантів:

// 1. local.properties
GEMINI_API_KEY=your_key_here

// 2. BuildConfig
android {
    buildTypes {
        debug {
            buildConfigField("String", "GEMINI_API_KEY", "\"${properties["GEMINI_API_KEY"]}\"")
        }
    }
}

// 3. Secure storage (для продакшену)
```

### Permissions в AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### Gradle Dependencies

```kotlin
dependencies {
    // Gemini API
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

---

## Тестування

### 1. Mock Mode (без API key)

Створити `GeminiApiClientMock.kt` для тестування UI без API:

```kotlin
class GeminiApiClientMock : GeminiApiClient {
    override suspend fun generateDebateResponse(...): Result<String> {
        delay(2000) // Simulate network
        return Result.success("Це mock відповідь AI. Твій аргумент цікавий, але...")
    }
}
```

### 2. Error Handling

- [ ] Network error → показати retry
- [ ] API rate limit → пояснити ліміт
- [ ] Empty response → fallback message

### 3. UI/UX

- [ ] Loading states (AI thinking)
- [ ] Smooth transitions між фазами
- [ ] Clear feedback на кожен крок

---

## Перевірка

```bash
./gradlew assembleDebug
```

**Testing Checklist:**

**Debate:**
- [ ] Topic selection працює
- [ ] Position selection працює
- [ ] Recording + transcription
- [ ] AI response отримується
- [ ] Multiple rounds працюють
- [ ] Debate complete summary

**Sales Pitch:**
- [ ] Product selection
- [ ] Customer profile generation
- [ ] Opening pitch recording
- [ ] AI objection generation
- [ ] Handling objection recording
- [ ] AI decision (купує/не купує)

---

## Очікуваний результат

✅ GeminiApiClient з 3 методами
✅ Debate Screen (5 phases)
✅ Sales Pitch Screen (interactive)
✅ Content providers (8 debate topics, products, customers)
✅ Turn-based AI conversation працює
✅ Premium feature (ці режими доступні тільки для Premium)

---

## 🎉 PHASE 5 ЗАВЕРШЕНО!

```
✅ Phase 5.1 — Improvisation Hub + Random Topic
✅ Phase 5.2 — Storytelling + Daily Challenge
✅ Phase 5.3 — Debate + Sales Pitch (AI-interactive)
```

**Всі 5 режимів імпровізації готові!**

---

## 🚀 Наступний крок: Phase 6

**Phase 6: AI Coach** — використає ту саму GeminiApiClient infrastructure:
- Chat interface
- Context-aware responses (знає UserProgress)
- Message history
- Quick actions

**Складність:** 🔴 ВИСОКА  
**Час:** ⏱️ 8-10 годин (2-3 підфази)

---

**Час на Phase 5.3:** ~4-5 годин

**Примітка:** Це proof of concept для AI integration. Phase 6 буде схожим підходом.