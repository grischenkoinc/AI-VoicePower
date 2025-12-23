# Промпт для Claude Code — Phase 5.2: Storytelling + Daily Challenge

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1-2.5 — Warmup
- ✅ Phase 3 — Home Screen
- ✅ Phase 4.1-4.4 — Courses (повністю)
- ✅ Phase 5.1 — Improvisation Hub + Random Topic

Зараз **Phase 5.2 — Storytelling + Daily Challenge** — друга підфаза Phase 5.

**Згідно з PHASE_STRUCTURE_GUIDE.md**: Середньої складності, креативні формати імпровізації.

**Специфікація:** `SPECIFICATION.md`, секції 4.3.7 (Improvisation Screen) + 5.5 (ImprovisationTask).

**Складність:** 🟡 СЕРЕДНЯ  
**Час:** ⏱️ 2 години

---

## Ключова ідея

**Phase 5.2** додає 2 нові режими імпровізації:

### 1. Storytelling (Розповідь історій)
4 формати:
- **З підказками** — герой, місце, предмет, твіст
- **За картинкою** — опис згенерованої сцени (поки текст)
- **Продовж історію** — початок історії, треба завершити
- **3 випадкові слова** — включити слова в розповідь

### 2. Daily Challenge (Щоденний челендж)
- Унікальне завдання кожен день
- Різні типи: тема, storytelling, емоція, обмеження
- Tracking completion у DailyChallengeEntity
- Badge за виконання

---

## Storytelling Flow

```
ImprovisationScreen
    │
    ▼
Click "Storytelling"
    │
    ▼
StorytellingScreen
    │
    ├─ Вибір формату (4 варіанти)
    │
    ▼
Генерація елементів історії
    │
    ├─ WITH_PROMPTS: герой, місце, предмет, твіст
    ├─ FROM_IMAGE: опис сцени
    ├─ CONTINUE: початок історії
    ├─ RANDOM_WORDS: 3 слова
    │
    ▼
30 секунд підготовка
    │
    ▼
Запис 2-4 хв
    │
    ▼
Збереження → RecordingDao
    │
    ▼
Navigate → Results Screen
```

---

## Daily Challenge Flow

```
ImprovisationScreen
    │
    ▼
Click "Щоденний челендж"
    │
    ▼
DailyChallengeScreen
    │
    ├─ Перевірка: чи є челендж на сьогодні?
    │
    ├─ Якщо НІ → генерувати новий (based on date seed)
    ├─ Якщо ТАК → показати існуючий
    │
    ▼
Показати челендж (з типом та інструкціями)
    │
    ▼
Виконання (підготовка + запис)
    │
    ▼
Mark completed в DailyChallengeDao
    │
    ▼
Navigate → Results Screen
```

---

## Структура файлів

```
ui/screens/improvisation/
├── StorytellingScreen.kt
├── StorytellingViewModel.kt
├── StorytellingState.kt
├── StorytellingEvent.kt
│
├── DailyChallengeScreen.kt
├── DailyChallengeViewModel.kt
├── DailyChallengeState.kt
├── DailyChallengeEvent.kt
│
└── components/
    ├── StoryFormatCard.kt
    ├── StoryElementsCard.kt
    └── ChallengeCard.kt

data/content/
├── StoryElementsProvider.kt
└── DailyChallengeProvider.kt
```

---

## Повний код

### 1. StoryElementsProvider.kt

```kotlin
package com.aivoicepower.data.content

import com.aivoicepower.domain.model.exercise.StoryFormat

/**
 * Provider для елементів історій
 */
object StoryElementsProvider {
    
    data class StoryElements(
        val format: StoryFormat,
        val hero: String? = null,
        val place: String? = null,
        val item: String? = null,
        val twist: String? = null,
        val sceneDescription: String? = null,
        val storyBeginning: String? = null,
        val randomWords: List<String>? = null
    )
    
    private val heroes = listOf(
        "детектив", "вчитель", "програміст", "космонавт", "шеф-кухар",
        "художник", "лікар", "музикант", "блогер", "археолог",
        "піцабот", "таксист", "письменник", "дизайнер", "спортсмен"
    )
    
    private val places = listOf(
        "покинута бібліотека", "космічна станція", "старовинний замок",
        "сучасний офіс", "таємничий ліс", "підводна база", "дах хмарочосу",
        "антикварна крамниця", "метро о 3 ночі", "парк атракціонів",
        "пекарня в маленькому місті", "музей природознавства", "recording studio"
    )
    
    private val items = listOf(
        "стара карта", "загадковий ключ", "фотографія", "лист від незнайомця",
        "зламаний годинник", "музична скринька", "старовинна книга",
        "чарівний амулет", "планшет з дивними даними", "записка з координатами",
        "старий мобільний телефон", "незвичайна монета", "пошкоджений диск"
    )
    
    private val twists = listOf(
        "раптом зник світ", "з'явилася людина з майбутнього",
        "герой виявляє приховану здатність", "місце виявляється ілюзією",
        "час починає йти назад", "герой зустрічає себе з минулого",
        "реальність виявляється симуляцією", "герой розуміє що спить",
        "всі люди навколо зникають", "починається несподівана подорож"
    )
    
    private val sceneDescriptions = listOf(
        "Порожній вагон метро, що мчить крізь тунель. На підлозі - загадкова сумка.",
        "Дах хмарочосу на світанку. Вдалині - силует незнайомої людини.",
        "Стара бібліотека після закриття. Одна книга світиться у темряві.",
        "Кафе біля вікна під час грози. За столиком - незнайомець з твоїм фото.",
        "Пустеля вночі під зоряним небом. Вдалині - таємничі вогні."
    )
    
    private val storyBeginnings = listOf(
        "Того ранку все почалося з дивного дзвінка на мобільний. Номер був невідомий, але голос здавався до болю знайомим...",
        "Я знайшов цей ключ у кишені куртки, яку купив у секонд-хенді. На бирці було написано адресу, якої не існувало на картах...",
        "Вона сказала мені три слова, які змінили все: 'У тебе є 24 години'. Тоді я ще не розумів, що це означає...",
        "Коли я прокинувся того ранку, моя квартира була повністю порожня. Але найдивнішим було інше - на стіні висіла картина, якої я ніколи не бачив..."
    )
    
    private val randomWordsSets = listOf(
        listOf("парасолька", "дзеркало", "кава"),
        listOf("блокнот", "світлофор", "мелодія"),
        listOf("годинник", "вікно", "таємниця"),
        listOf("телефон", "дощ", "спогад"),
        listOf("ключ", "двері", "майбутнє"),
        listOf("книга", "вогонь", "зустріч"),
        listOf("листок", "вітер", "рішення")
    )
    
    fun generateStoryElements(format: StoryFormat): StoryElements {
        return when (format) {
            StoryFormat.WITH_PROMPTS -> StoryElements(
                format = format,
                hero = heroes.random(),
                place = places.random(),
                item = items.random(),
                twist = twists.random()
            )
            StoryFormat.FROM_IMAGE -> StoryElements(
                format = format,
                sceneDescription = sceneDescriptions.random()
            )
            StoryFormat.CONTINUE -> StoryElements(
                format = format,
                storyBeginning = storyBeginnings.random()
            )
            StoryFormat.RANDOM_WORDS -> StoryElements(
                format = format,
                randomWords = randomWordsSets.random()
            )
        }
    }
}
```

### 2. DailyChallengeProvider.kt

```kotlin
package com.aivoicepower.data.content

import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * Provider для щоденних челенджів
 */
object DailyChallengeProvider {
    
    data class DailyChallenge(
        val id: String,
        val date: String,
        val type: ChallengeType,
        val title: String,
        val description: String,
        val instruction: String,
        val duration: Int,
        val difficulty: String
    )
    
    enum class ChallengeType {
        TOPIC,          // Тема для обговорення
        STORYTELLING,   // Розповідь історії
        EMOTION,        // Говорити з емоцією
        CONSTRAINT,     // З обмеженням (без "я", без пауз)
        SPEED,          // Швидко/повільно
        PERSUASION      // Переконання
    }
    
    private val topicChallenges = listOf(
        "Розкажи про найважливіший урок, який ти отримав/отримала цього року",
        "Опиши ідеальний день з погляду продуктивності",
        "Переконай слухачів, чому варто вивчати нову навичку після 30",
        "Розкажи про технологію майбутнього, яку ти б хотів/хотіла побачити",
        "Опиши місце, де ти відчуваєш себе найщасливішим/найщасливішою"
    )
    
    private val storytellingChallenges = listOf(
        "Розкажи історію про випадкову зустріч, що змінила чиєсь життя",
        "Створи детективну історію про зниклий артефакт",
        "Розкажи казку для дорослих про пошук сенсу життя",
        "Опиши день з життя звичайного предмета (чашка, телефон, ключ)"
    )
    
    private val emotionChallenges = listOf(
        "Розкажи про свій день надзвичайно ентузіазним тоном",
        "Опиши рецепт страви драматичним шекспірівським стилем",
        "Поясни, як користуватися смартфоном, наче це найскладніша річ",
        "Розкажи про похід до магазину як про епічну пригоду"
    )
    
    private val constraintChallenges = listOf(
        "Говори 2 хвилини без використання слова 'я' та 'мені'",
        "Опиши свій день без пауз довше 1 секунди",
        "Розкажи історію, використовуючи тільки короткі речення (максимум 7 слів)",
        "Говори про технології без використання англійських слів"
    )
    
    private val speedChallenges = listOf(
        "Розкажи про улюблений фільм дуже повільно та виразно",
        "Опиши свій ранок максимально швидко, але чітко",
        "Поясни складну концепцію повільно, наче дитині"
    )
    
    private val persuasionChallenges = listOf(
        "Переконай слухачів, що 4-денний робочий тиждень - це майбутнє",
        "Доведи, що книги кращі за фільми (або навпаки)",
        "Аргументуй, чому варто відмовитися від соцмереж на місяць",
        "Переконай скептика спробувати нову активність"
    )
    
    /**
     * Генерує челендж на основі дати (детермінований)
     */
    fun getChallengeForDate(date: String): DailyChallenge {
        // Use date as seed for deterministic randomness
        val seed = date.hashCode().toLong()
        val random = Random(seed)
        
        val type = ChallengeType.values()[random.nextInt(ChallengeType.values().size)]
        
        val (title, description, instruction) = when (type) {
            ChallengeType.TOPIC -> {
                val challenge = topicChallenges[random.nextInt(topicChallenges.size)]
                Triple(
                    "Тематичний виступ",
                    challenge,
                    "Структуруй свою розповідь: вступ, основна частина, висновок"
                )
            }
            ChallengeType.STORYTELLING -> {
                val challenge = storytellingChallenges[random.nextInt(storytellingChallenges.size)]
                Triple(
                    "Storytelling",
                    challenge,
                    "Використай драматургічну структуру: зав'язка, розвиток, кульмінація, розв'язка"
                )
            }
            ChallengeType.EMOTION -> {
                val challenge = emotionChallenges[random.nextInt(emotionChallenges.size)]
                Triple(
                    "Емоційний виклик",
                    challenge,
                    "Використовуй інтонацію, паузи та емоційні акценти"
                )
            }
            ChallengeType.CONSTRAINT -> {
                val challenge = constraintChallenges[random.nextInt(constraintChallenges.size)]
                Triple(
                    "Виклик з обмеженням",
                    challenge,
                    "Дотримуйся правил, але говори природно"
                )
            }
            ChallengeType.SPEED -> {
                val challenge = speedChallenges[random.nextInt(speedChallenges.size)]
                Triple(
                    "Темп мовлення",
                    challenge,
                    "Стеж за темпом, але не втрачай чіткості"
                )
            }
            ChallengeType.PERSUASION -> {
                val challenge = persuasionChallenges[random.nextInt(persuasionChallenges.size)]
                Triple(
                    "Переконання",
                    challenge,
                    "Використовуй факти, логіку та емоційний зв'язок"
                )
            }
        }
        
        val duration = when (type) {
            ChallengeType.CONSTRAINT, ChallengeType.SPEED -> 120 // 2 min
            else -> 180 // 3 min
        }
        
        val difficulty = when (type) {
            ChallengeType.TOPIC -> "Середня"
            ChallengeType.STORYTELLING -> "Середня"
            ChallengeType.EMOTION -> "Легка"
            ChallengeType.CONSTRAINT -> "Складна"
            ChallengeType.SPEED -> "Середня"
            ChallengeType.PERSUASION -> "Складна"
        }
        
        return DailyChallenge(
            id = "challenge_$date",
            date = date,
            type = type,
            title = title,
            description = description,
            instruction = instruction,
            duration = duration,
            difficulty = difficulty
        )
    }
    
    fun getTodayChallenge(): DailyChallenge {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return getChallengeForDate(today)
    }
}
```

### 3. StorytellingState.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.content.StoryElementsProvider
import com.aivoicepower.domain.model.exercise.StoryFormat

data class StorytellingState(
    val selectedFormat: StoryFormat? = null,
    val storyElements: StoryElementsProvider.StoryElements? = null,
    val phase: StorytellingPhase = StorytellingPhase.FormatSelection,
    val preparationSecondsLeft: Int = 30,
    val recordingSecondsElapsed: Int = 0,
    val maxDuration: Int = 180,
    val recordingPath: String? = null,
    val isRecording: Boolean = false,
    val error: String? = null
)

sealed class StorytellingPhase {
    object FormatSelection : StorytellingPhase()
    object Elements : StorytellingPhase()
    object Preparation : StorytellingPhase()
    object Recording : StorytellingPhase()
    object Completed : StorytellingPhase()
}
```

### 4. StorytellingEvent.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.domain.model.exercise.StoryFormat

sealed class StorytellingEvent {
    data class FormatSelected(val format: StoryFormat) : StorytellingEvent()
    object GenerateElementsClicked : StorytellingEvent()
    object StartPreparationClicked : StorytellingEvent()
    object StartRecordingClicked : StorytellingEvent()
    object StopRecordingClicked : StorytellingEvent()
    object SaveAndFinishClicked : StorytellingEvent()
}
```

### 5. StorytellingViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.StoryElementsProvider
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.model.exercise.StoryFormat
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StorytellingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordingDao: RecordingDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(StorytellingState())
    val state: StateFlow<StorytellingState> = _state.asStateFlow()
    
    private val audioRecorder = AudioRecorderUtil(context)
    
    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
    }
    
    fun onEvent(event: StorytellingEvent) {
        when (event) {
            is StorytellingEvent.FormatSelected -> {
                _state.update { it.copy(selectedFormat = event.format) }
            }
            StorytellingEvent.GenerateElementsClicked -> {
                generateElements()
            }
            StorytellingEvent.StartPreparationClicked -> {
                startPreparation()
            }
            StorytellingEvent.StartRecordingClicked -> {
                startRecording()
            }
            StorytellingEvent.StopRecordingClicked -> {
                stopRecording()
            }
            StorytellingEvent.SaveAndFinishClicked -> {
                saveRecording()
            }
        }
    }
    
    private fun generateElements() {
        val format = _state.value.selectedFormat ?: return
        val elements = StoryElementsProvider.generateStoryElements(format)
        _state.update {
            it.copy(
                storyElements = elements,
                phase = StorytellingPhase.Elements
            )
        }
    }
    
    private fun startPreparation() {
        _state.update { it.copy(phase = StorytellingPhase.Preparation, preparationSecondsLeft = 30) }
        
        viewModelScope.launch {
            repeat(30) {
                delay(1000)
                _state.update { it.copy(preparationSecondsLeft = it.preparationSecondsLeft - 1) }
            }
            _state.update { it.copy(phase = StorytellingPhase.Recording) }
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
                        recordingSecondsElapsed = 0,
                        recordingPath = outputFile.absolutePath
                    )
                }
                
                // Timer
                val maxSeconds = _state.value.maxDuration
                var elapsed = 0
                while (elapsed < maxSeconds && _state.value.isRecording) {
                    delay(1000)
                    elapsed++
                    _state.update { it.copy(recordingSecondsElapsed = elapsed) }
                }
                
                if (elapsed >= maxSeconds) {
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
                _state.update {
                    it.copy(
                        isRecording = false,
                        phase = StorytellingPhase.Completed
                    )
                }
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
    
    private fun saveRecording() {
        viewModelScope.launch {
            try {
                val recordingPath = _state.value.recordingPath ?: return@launch
                val format = _state.value.selectedFormat ?: return@launch
                
                val recordingEntity = RecordingEntity(
                    id = UUID.randomUUID().toString(),
                    filePath = recordingPath,
                    durationMs = _state.value.recordingSecondsElapsed * 1000L,
                    type = "improvisation",
                    contextId = "storytelling_${format.name}",
                    isAnalyzed = false
                )
                
                recordingDao.insert(recordingEntity)
                userPreferencesDataStore.incrementFreeImprovisations()
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Помилка збереження: ${e.message}")
                }
            }
        }
    }
}
```

### 6. StorytellingScreen.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.domain.model.exercise.StoryFormat
import com.aivoicepower.ui.screens.improvisation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorytellingScreen(
    viewModel: StorytellingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storytelling") },
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
                StorytellingPhase.FormatSelection -> {
                    FormatSelectionContent(
                        selectedFormat = state.selectedFormat,
                        onFormatSelected = { viewModel.onEvent(StorytellingEvent.FormatSelected(it)) },
                        onGenerate = { viewModel.onEvent(StorytellingEvent.GenerateElementsClicked) }
                    )
                }
                
                StorytellingPhase.Elements -> {
                    StoryElementsContent(
                        storyElements = state.storyElements!!,
                        onStart = { viewModel.onEvent(StorytellingEvent.StartPreparationClicked) },
                        onRegenerate = { viewModel.onEvent(StorytellingEvent.GenerateElementsClicked) }
                    )
                }
                
                StorytellingPhase.Preparation -> {
                    com.aivoicepower.ui.screens.improvisation.components.PreparationTimer(
                        title = "Підготовка до розповіді",
                        secondsLeft = state.preparationSecondsLeft,
                        hint = "Продумай структуру своєї історії"
                    )
                }
                
                StorytellingPhase.Recording -> {
                    StoryRecordingContent(
                        isRecording = state.isRecording,
                        secondsElapsed = state.recordingSecondsElapsed,
                        maxSeconds = state.maxDuration,
                        onStart = { viewModel.onEvent(StorytellingEvent.StartRecordingClicked) },
                        onStop = { viewModel.onEvent(StorytellingEvent.StopRecordingClicked) }
                    )
                }
                
                StorytellingPhase.Completed -> {
                    com.aivoicepower.ui.screens.improvisation.components.CompletedPhaseContent(
                        durationSeconds = state.recordingSecondsElapsed,
                        onSave = {
                            viewModel.onEvent(StorytellingEvent.SaveAndFinishClicked)
                            onNavigateBack()
                        }
                    )
                }
            }
        }
    }
}
```

### 7. DailyChallengeState.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.data.content.DailyChallengeProvider

data class DailyChallengeState(
    val challenge: DailyChallengeProvider.DailyChallenge? = null,
    val isCompleted: Boolean = false,
    val phase: ChallengePhase = ChallengePhase.Loading,
    val preparationSecondsLeft: Int = 30,
    val recordingSecondsElapsed: Int = 0,
    val recordingPath: String? = null,
    val isRecording: Boolean = false,
    val error: String? = null
)

sealed class ChallengePhase {
    object Loading : ChallengePhase()
    object Challenge : ChallengePhase()
    object AlreadyCompleted : ChallengePhase()
    object Preparation : ChallengePhase()
    object Recording : ChallengePhase()
    object Completed : ChallengePhase()
}
```

### 8. DailyChallengeEvent.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

sealed class DailyChallengeEvent {
    object StartPreparationClicked : DailyChallengeEvent()
    object StartRecordingClicked : DailyChallengeEvent()
    object StopRecordingClicked : DailyChallengeEvent()
    object SaveAndFinishClicked : DailyChallengeEvent()
}
```

### 9. DailyChallengeViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.DailyChallengeProvider
import com.aivoicepower.data.local.database.dao.DailyChallengeDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.DailyChallengeEntity
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DailyChallengeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dailyChallengeDao: DailyChallengeDao,
    private val recordingDao: RecordingDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(DailyChallengeState())
    val state: StateFlow<DailyChallengeState> = _state.asStateFlow()
    
    private val audioRecorder = AudioRecorderUtil(context)
    
    init {
        loadTodayChallenge()
    }
    
    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
    }
    
    fun onEvent(event: DailyChallengeEvent) {
        when (event) {
            DailyChallengeEvent.StartPreparationClicked -> {
                startPreparation()
            }
            DailyChallengeEvent.StartRecordingClicked -> {
                startRecording()
            }
            DailyChallengeEvent.StopRecordingClicked -> {
                stopRecording()
            }
            DailyChallengeEvent.SaveAndFinishClicked -> {
                saveRecording()
            }
        }
    }
    
    private fun loadTodayChallenge() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            try {
                // Check if already completed
                val existing = dailyChallengeDao.getChallengeForDateOnce(today)
                
                if (existing != null && existing.isCompleted) {
                    // Already completed today
                    val challenge = DailyChallengeProvider.getChallengeForDate(today)
                    _state.update {
                        it.copy(
                            challenge = challenge,
                            isCompleted = true,
                            phase = ChallengePhase.AlreadyCompleted
                        )
                    }
                } else {
                    // New challenge or not completed
                    val challenge = DailyChallengeProvider.getTodayChallenge()
                    
                    // Save to DB if not exists
                    if (existing == null) {
                        dailyChallengeDao.insertOrUpdate(
                            DailyChallengeEntity(
                                date = today,
                                challengeId = challenge.id,
                                isCompleted = false
                            )
                        )
                    }
                    
                    _state.update {
                        it.copy(
                            challenge = challenge,
                            isCompleted = false,
                            phase = ChallengePhase.Challenge
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка завантаження: ${e.message}",
                        phase = ChallengePhase.Challenge
                    )
                }
            }
        }
    }
    
    private fun startPreparation() {
        _state.update { it.copy(phase = ChallengePhase.Preparation, preparationSecondsLeft = 30) }
        
        viewModelScope.launch {
            repeat(30) {
                delay(1000)
                _state.update { it.copy(preparationSecondsLeft = it.preparationSecondsLeft - 1) }
            }
            _state.update { it.copy(phase = ChallengePhase.Recording) }
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
                        recordingSecondsElapsed = 0,
                        recordingPath = outputFile.absolutePath
                    )
                }
                
                // Timer
                val maxSeconds = _state.value.challenge?.duration ?: 180
                var elapsed = 0
                while (elapsed < maxSeconds && _state.value.isRecording) {
                    delay(1000)
                    elapsed++
                    _state.update { it.copy(recordingSecondsElapsed = elapsed) }
                }
                
                if (elapsed >= maxSeconds) {
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
                _state.update {
                    it.copy(
                        isRecording = false,
                        phase = ChallengePhase.Completed
                    )
                }
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
    
    private fun saveRecording() {
        viewModelScope.launch {
            try {
                val recordingPath = _state.value.recordingPath ?: return@launch
                val challenge = _state.value.challenge ?: return@launch
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                
                val recordingId = UUID.randomUUID().toString()
                
                val recordingEntity = RecordingEntity(
                    id = recordingId,
                    filePath = recordingPath,
                    durationMs = _state.value.recordingSecondsElapsed * 1000L,
                    type = "improvisation",
                    contextId = "daily_challenge",
                    exerciseId = challenge.id,
                    isAnalyzed = false
                )
                
                recordingDao.insert(recordingEntity)
                
                // Mark challenge as completed
                dailyChallengeDao.markCompleted(today, recordingId)
                
                userPreferencesDataStore.incrementFreeImprovisations()
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Помилка збереження: ${e.message}")
                }
            }
        }
    }
}
```

### 10. DailyChallengeScreen.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.improvisation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    viewModel: DailyChallengeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Щоденний челендж") },
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
                ChallengePhase.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                ChallengePhase.Challenge -> {
                    ChallengeContent(
                        challenge = state.challenge!!,
                        onStart = { viewModel.onEvent(DailyChallengeEvent.StartPreparationClicked) }
                    )
                }
                
                ChallengePhase.AlreadyCompleted -> {
                    AlreadyCompletedContent(
                        challenge = state.challenge!!,
                        onBack = onNavigateBack
                    )
                }
                
                ChallengePhase.Preparation -> {
                    PreparationTimer(
                        title = "Підготовка",
                        secondsLeft = state.preparationSecondsLeft,
                        hint = state.challenge?.instruction ?: ""
                    )
                }
                
                ChallengePhase.Recording -> {
                    ChallengeRecordingContent(
                        challenge = state.challenge!!,
                        isRecording = state.isRecording,
                        secondsElapsed = state.recordingSecondsElapsed,
                        maxSeconds = state.challenge?.duration ?: 180,
                        onStart = { viewModel.onEvent(DailyChallengeEvent.StartRecordingClicked) },
                        onStop = { viewModel.onEvent(DailyChallengeEvent.StopRecordingClicked) }
                    )
                }
                
                ChallengePhase.Completed -> {
                    CompletedPhaseContent(
                        durationSeconds = state.recordingSecondsElapsed,
                        onSave = {
                            viewModel.onEvent(DailyChallengeEvent.SaveAndFinishClicked)
                            onNavigateBack()
                        }
                    )
                }
            }
        }
    }
}
```

### 11. Компоненти (скорочена версія - основні)

#### components/StoryFormatCard.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.exercise.StoryFormat

@Composable
fun FormatSelectionContent(
    selectedFormat: StoryFormat?,
    onFormatSelected: (StoryFormat) -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Обери формат історії",
            style = MaterialTheme.typography.titleLarge
        )
        
        StoryFormat.values().forEach { format ->
            FilterChip(
                selected = selectedFormat == format,
                onClick = { onFormatSelected(format) },
                label = {
                    Column {
                        Text(
                            text = when (format) {
                                StoryFormat.WITH_PROMPTS -> "🎭 З підказками"
                                StoryFormat.FROM_IMAGE -> "🖼️ За сценою"
                                StoryFormat.CONTINUE -> "📝 Продовж історію"
                                StoryFormat.RANDOM_WORDS -> "🎲 3 випадкові слова"
                            },
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = when (format) {
                                StoryFormat.WITH_PROMPTS -> "Герой, місце, предмет, твіст"
                                StoryFormat.FROM_IMAGE -> "Опиши сцену та розкажи історію"
                                StoryFormat.CONTINUE -> "Завер��и історію, що почалася"
                                StoryFormat.RANDOM_WORDS -> "Використай слова в розповіді"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedFormat != null
        ) {
            Text("Згенерувати елементи")
        }
    }
}
```

#### components/StoryElementsCard.kt & ChallengeCard.kt - створити аналогічно TopicCard з Phase 5.1

---

## Оновити NavGraph.kt

```kotlin
composable(NavRoutes.Storytelling.route) {
    StorytellingScreen(
        onNavigateBack = { navController.popBackStack() },
        onNavigateToResults = { recordingId ->
            navController.navigate(NavRoutes.Results.createRoute(recordingId))
        }
    )
}

composable(NavRoutes.DailyChallenge.route) {
    DailyChallengeScreen(
        onNavigateBack = { navController.popBackStack() },
        onNavigateToResults = { recordingId ->
            navController.navigate(NavRoutes.Results.createRoute(recordingId))
        }
    )
}
```

## Оновити ImprovisationScreen.kt

Змінити `isEnabled` та `comingSoon`:

```kotlin
// Storytelling - тепер ready
ImprovisationModeCard(
    emoji = "📖",
    title = "Storytelling",
    description = "Розкажи історію за заданими елементами",
    isEnabled = viewModel.canStartImprovisation(),
    comingSoon = false,  // Змінено!
    onClick = onNavigateToStorytelling
)

// Daily Challenge - тепер ready
ImprovisationModeCard(
    emoji = "🎯",
    title = "Щоденний челендж",
    description = "Унікальне завдання кожен день",
    isEnabled = viewModel.canStartImprovisation(),
    comingSoon = false,  // Змінено!
    onClick = onNavigateToChallenge
)
```

---

## Перевірка

### 1. Компіляція
```bash
./gradlew assembleDebug
```

### 2. Testing Flow

**Storytelling:**
- [ ] Format selection працює
- [ ] Elements generation для кожного формату
- [ ] Preparation 30 сек
- [ ] Recording + save

**Daily Challenge:**
- [ ] Завантажує today challenge
- [ ] Детермінований (той самий challenge для дати)
- [ ] Mark completed працює
- [ ] "Already completed" показується

---

## Очікуваний результат

✅ StorytellingScreen з 4 форматами
✅ DailyChallengeScreen з tracking
✅ Content providers
✅ Navigation готова

**Час на Phase 5.2:** ~2 години