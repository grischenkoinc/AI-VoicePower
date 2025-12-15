# AI VoicePower — Специфікація v2.0

> Професійний застосунок для покращення мовлення та тренування публічних виступів з AI-наставником

---

## 📋 Зміст

1. [Загальне бачення](#1-загальне-бачення)
2. [Технічний стек](#2-технічний-стек)
3. [Архітектура проекту](#3-архітектура-проекту)
4. [Структура екранів та навігація](#4-структура-екранів-та-навігація)
5. [Domain моделі](#5-domain-моделі)
6. [Фази розробки](#6-фази-розробки)
7. [Freemium модель](#7-freemium-модель)
8. [AI інтеграція](#8-ai-інтеграція)
9. [Дизайн система](#9-дизайн-система)

---

## 1. Загальне бачення

### 1.1 Опис продукту

AI VoicePower — це Android-застосунок для:
- Покращення дикції та чіткості мовлення
- Тренування публічних виступів
- Розвитку впевненості у спілкуванні
- Позбавлення від слів-паразитів
- Покращення інтонації та виразності

### 1.2 Цільова аудиторія

- Люди, які хочуть говорити чіткіше
- Публічні спікери та презентатори
- Люди, що готуються до співбесід
- Студенти та викладачі
- Продавці та менеджери
- Всі, хто хоче покращити комунікаційні навички

### 1.3 Ключові особливості

| Функція | Опис |
|---------|------|
| **Діагностика** | AI визначає рівень користувача та слабкі місця |
| **Персоналізація** | Індивідуальний план на основі діагностики |
| **Розминка** | Щоденні вправи для мовленнєвого апарату (без AI-аналізу) |
| **Тематичні курси** | Структуровані програми по 21 дню |
| **Імпровізація** | Спонтанне мовлення, дебати з AI, продажі |
| **AI-тренер** | Чат-бот для порад та підготовки до виступів |
| **Прогрес** | Детальна статистика та порівняння "до/після" |

### 1.4 Унікальна цінність (USP)

- **Розминка без аудіо-аналізу** — логічний підхід (не записуємо дихання)
- **Діагностика на старті** — персоналізований досвід з першого дня
- **AI-дебати та продажі** — унікальні режими практики
- **Порівняння "до/після"** — наочний прогрес

---

## 2. Технічний стек

### 2.1 Основні технології

| Технологія | Версія | Призначення |
|------------|--------|-------------|
| Kotlin | 1.9+ | Мова програмування |
| Jetpack Compose | 1.5+ | UI Framework |
| Hilt | 2.48+ | Dependency Injection |
| Navigation Compose | 2.7+ | Навігація |
| Room | 2.6+ | Локальна база даних |
| DataStore | 1.0+ | Налаштування користувача |
| Coroutines | 1.7+ | Асинхронність |
| Flow | - | Реактивні потоки даних |

### 2.2 AI та медіа

| Технологія | Призначення |
|------------|-------------|
| Google Gemini API | AI-аналіз, чат, генерація контенту |
| Android SpeechRecognizer | Speech-to-Text (безкоштовно) |
| MediaRecorder / AudioRecord | Запис аудіо |
| ExoPlayer / Media3 | Відтворення аудіо/відео |

### 2.3 Додаткові бібліотеки

```kotlin
// Рекомендовані залежності
implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
implementation("androidx.datastore:datastore-preferences:1.0.0")
implementation("io.coil-kt:coil-compose:2.5.0") // Для зображень
implementation("com.airbnb.android:lottie-compose:6.3.0") // Для анімацій
```

### 2.4 Мінімальні вимоги

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

---

## 3. Архітектура проекту

### 3.1 Загальна структура (Clean Architecture + MVVM)

```
app/src/main/java/com/aivoicepower/
│
├── data/                           # DATA LAYER
│   ├── local/                      # Локальне збереження
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── UserProgressDao.kt
│   │   │   │   ├── CourseProgressDao.kt
│   │   │   │   ├── RecordingDao.kt
│   │   │   │   └── AchievementDao.kt
│   │   │   └── entity/
│   │   │       ├── UserProgressEntity.kt
│   │   │       ├── CourseProgressEntity.kt
│   │   │       ├── RecordingEntity.kt
│   │   │       └── AchievementEntity.kt
│   │   └── datastore/
│   │       └── UserPreferencesDataStore.kt
│   │
│   ├── remote/                     # Віддалені джерела
│   │   └── GeminiApiClient.kt
│   │
│   ├── repository/                 # Імплементації репозиторіїв
│   │   ├── UserRepositoryImpl.kt
│   │   ├── CourseRepositoryImpl.kt
│   │   ├── ExerciseRepositoryImpl.kt
│   │   ├── RecordingRepositoryImpl.kt
│   │   └── AiCoachRepositoryImpl.kt
│   │
│   └── model/                      # Data моделі (DTO, Entity)
│       └── ...
│
├── domain/                         # DOMAIN LAYER
│   ├── model/                      # Бізнес-моделі
│   │   ├── user/
│   │   │   ├── UserProfile.kt
│   │   │   ├── UserProgress.kt
│   │   │   ├── DiagnosticResult.kt
│   │   │   └── Achievement.kt
│   │   │
│   │   ├── course/
│   │   │   ├── Course.kt
│   │   │   ├── Lesson.kt
│   │   │   └── LessonProgress.kt
│   │   │
│   │   ├── exercise/
│   │   │   ├── Exercise.kt
│   │   │   ├── ExerciseType.kt
│   │   │   ├── WarmupExercise.kt
│   │   │   └── ImprovisationTask.kt
│   │   │
│   │   ├── analysis/
│   │   │   ├── VoiceAnalysis.kt
│   │   │   ├── AnalysisMetric.kt
│   │   │   └── Feedback.kt
│   │   │
│   │   └── content/
│   │       ├── TongueTwister.kt
│   │       ├── ReadingText.kt
│   │       ├── DebateTopic.kt
│   │       └── SalesProduct.kt
│   │
│   ├── repository/                 # Інтерфейси репозиторіїв
│   │   ├── UserRepository.kt
│   │   ├── CourseRepository.kt
│   │   ├── ExerciseRepository.kt
│   │   ├── RecordingRepository.kt
│   │   └── AiCoachRepository.kt
│   │
│   └── usecase/                    # Use cases (опціонально)
│       ├── GetUserProgressUseCase.kt
│       ├── AnalyzeRecordingUseCase.kt
│       └── ...
│
├── ui/                             # PRESENTATION LAYER
│   ├── navigation/
│   │   ├── NavGraph.kt
│   │   ├── NavRoutes.kt
│   │   └── NavActions.kt
│   │
│   ├── screens/
│   │   ├── onboarding/             # Онбординг
│   │   │   ├── OnboardingScreen.kt
│   │   │   └── OnboardingViewModel.kt
│   │   │
│   │   ├── diagnostic/             # Діагностика
│   │   │   ├── DiagnosticScreen.kt
│   │   │   ├── DiagnosticViewModel.kt
│   │   │   └── DiagnosticResultScreen.kt
│   │   │
│   │   ├── home/                   # Головний екран
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   │
│   │   ├── warmup/                 # Розминка
│   │   │   ├── WarmupScreen.kt
│   │   │   ├── WarmupViewModel.kt
│   │   │   ├── ArticulationScreen.kt
│   │   │   ├── BreathingScreen.kt
│   │   │   └── VoiceWarmupScreen.kt
│   │   │
│   │   ├── courses/                # Курси
│   │   │   ├── CoursesListScreen.kt
│   │   │   ├── CourseDetailScreen.kt
│   │   │   ├── LessonScreen.kt
│   │   │   └── CoursesViewModel.kt
│   │   │
│   │   ├── improvisation/          # Імпровізація
│   │   │   ├── ImprovisationScreen.kt
│   │   │   ├── ImprovisationViewModel.kt
│   │   │   ├── RandomTopicScreen.kt
│   │   │   ├── StorytellingScreen.kt
│   │   │   ├── DebateScreen.kt
│   │   │   └── SalesPitchScreen.kt
│   │   │
│   │   ├── aicoach/                # AI-тренер
│   │   │   ├── AiCoachScreen.kt
│   │   │   └── AiCoachViewModel.kt
│   │   │
│   │   ├── progress/               # Прогрес
│   │   │   ├── ProgressScreen.kt
│   │   │   ├── ProgressViewModel.kt
│   │   │   ├── CompareScreen.kt
│   │   │   └── AchievementsScreen.kt
│   │   │
│   │   ├── results/                # Результати аналізу
│   │   │   ├── ResultsScreen.kt
│   │   │   └── ResultsViewModel.kt
│   │   │
│   │   └── settings/               # Налаштування
│   │       ├── SettingsScreen.kt
│   │       └── SettingsViewModel.kt
│   │
│   ├── components/                 # Переспільні компоненти
│   │   ├── buttons/
│   │   │   ├── PrimaryButton.kt
│   │   │   ├── SecondaryButton.kt
│   │   │   └── RecordButton.kt
│   │   │
│   │   ├── cards/
│   │   │   ├── LessonCard.kt
│   │   │   ├── CourseCard.kt
│   │   │   ├── ExerciseCard.kt
│   │   │   └── AchievementCard.kt
│   │   │
│   │   ├── progress/
│   │   │   ├── CircularProgress.kt
│   │   │   ├── LinearProgress.kt
│   │   │   ├── SkillRadar.kt
│   │   │   └── StreakIndicator.kt
│   │   │
│   │   ├── timer/
│   │   │   ├── CountdownTimer.kt
│   │   │   ├── RecordingTimer.kt
│   │   │   └── BreathingTimer.kt
│   │   │
│   │   ├── audio/
│   │   │   ├── AudioPlayer.kt
│   │   │   ├── AudioRecorder.kt
│   │   │   ├── WaveformVisualizer.kt
│   │   │   └── VolumeIndicator.kt
│   │   │
│   │   ├── feedback/
│   │   │   ├── AiFeedbackCard.kt
│   │   │   ├── MetricBar.kt
│   │   │   └── TipCard.kt
│   │   │
│   │   └── common/
│   │       ├── LoadingIndicator.kt
│   │       ├── ErrorMessage.kt
│   │       ├── EmptyState.kt
│   │       └── TopAppBar.kt
│   │
│   └── theme/
│       ├── Color.kt
│       ├── Type.kt
│       ├── Shape.kt
│       └── Theme.kt
│
├── di/                             # Dependency Injection
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   └── AiModule.kt
│
├── utils/                          # Утиліти
│   ├── audio/
│   │   ├── AudioRecorderUtil.kt
│   │   ├── AudioPlayerUtil.kt
│   │   └── SpeechRecognizerUtil.kt
│   │
│   ├── extensions/
│   │   ├── ContextExtensions.kt
│   │   ├── StringExtensions.kt
│   │   └── FlowExtensions.kt
│   │
│   ├── formatters/
│   │   ├── TimeFormatter.kt
│   │   └── NumberFormatter.kt
│   │
│   └── constants/
│       ├── AppConstants.kt
│       └── AnalyticsConstants.kt
│
├── MainActivity.kt
└── VoicePowerApp.kt
```

### 3.2 Принципи архітектури

1. **Unidirectional Data Flow (UDF)**
   - UI → ViewModel → Repository → Data Source
   - Data Source → Repository → ViewModel → UI (через Flow)

2. **Single Source of Truth**
   - Room Database — для прогресу та записів
   - DataStore — для налаштувань
   - Content Provider — для статичного контенту (курси, вправи)

3. **Separation of Concerns**
   - UI не знає про джерела даних
   - ViewModel не знає про UI framework
   - Repository абстрагує джерела даних

---

## 4. Структура екранів та навігація

### 4.1 Карта навігації

```
┌─────────────────────────────────────────────────────────────────┐
│                        НАВІГАЦІЯ                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐                                               │
│  │  SPLASH      │ (перевірка чи пройдено онбординг)             │
│  └──────┬───────┘                                               │
│         │                                                        │
│         ├─── Новий користувач ───► ONBOARDING ───► DIAGNOSTIC   │
│         │                                              │         │
│         │                                              ▼         │
│         └─── Існуючий користувач ───────────────────► HOME      │
│                                                         │        │
│  ┌──────────────────────────────────────────────────────┼───┐   │
│  │                    BOTTOM NAVIGATION                  │   │   │
│  ├───────────┬───────────┬───────────┬───────────┬──────┴───┤   │
│  │   HOME    │  COURSES  │  WARMUP   │  IMPROV   │ PROGRESS │   │
│  │  (план)   │  (курси)  │ (розмін.) │ (імпров.) │  (стат.) │   │
│  └─────┬─────┴─────┬─────┴─────┬─────┴─────┬─────┴────┬─────┘   │
│        │           │           │           │          │          │
│        ▼           ▼           ▼           ▼          ▼          │
│   AI_COACH    COURSE_     ARTICUL.   RANDOM_    COMPARE         │
│   (floating   DETAIL      BREATHING  TOPIC      ACHIEVE-        │
│    button)    LESSON      VOICE      STORY      MENTS           │
│               RESULTS                DEBATE                      │
│                                      SALES                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 Routes (NavRoutes.kt)

```kotlin
sealed class NavRoutes(val route: String) {
    // Онбординг
    object Splash : NavRoutes("splash")
    object Onboarding : NavRoutes("onboarding")
    object Diagnostic : NavRoutes("diagnostic")
    object DiagnosticResult : NavRoutes("diagnostic_result")
    
    // Головні таби
    object Home : NavRoutes("home")
    object Courses : NavRoutes("courses")
    object Warmup : NavRoutes("warmup")
    object Improvisation : NavRoutes("improvisation")
    object Progress : NavRoutes("progress")
    
    // Розминка
    object Articulation : NavRoutes("warmup/articulation")
    object Breathing : NavRoutes("warmup/breathing")
    object VoiceWarmup : NavRoutes("warmup/voice")
    object QuickWarmup : NavRoutes("warmup/quick")
    
    // Курси
    object CourseDetail : NavRoutes("courses/{courseId}") {
        fun createRoute(courseId: String) = "courses/$courseId"
    }
    object Lesson : NavRoutes("courses/{courseId}/lesson/{lessonId}") {
        fun createRoute(courseId: String, lessonId: String) = "courses/$courseId/lesson/$lessonId"
    }
    
    // Імпровізація
    object RandomTopic : NavRoutes("improvisation/random")
    object Storytelling : NavRoutes("improvisation/story")
    object Debate : NavRoutes("improvisation/debate")
    object SalesPitch : NavRoutes("improvisation/sales")
    object DailyChallenge : NavRoutes("improvisation/challenge")
    
    // AI Coach
    object AiCoach : NavRoutes("ai_coach")
    
    // Результати
    object Results : NavRoutes("results/{recordingId}") {
        fun createRoute(recordingId: String) = "results/$recordingId"
    }
    
    // Прогрес
    object Compare : NavRoutes("progress/compare")
    object Achievements : NavRoutes("progress/achievements")
    
    // Налаштування
    object Settings : NavRoutes("settings")
}
```

### 4.3 Опис ключових екранів

#### 4.3.1 Onboarding Screen

**Мета:** Познайомити користувача з застосунком, зібрати базову інформацію.

**Складається з 4 сторінок (Pager):**

| Сторінка | Контент |
|----------|---------|
| 1 | Вітання + опис можливостей |
| 2 | "Яка твоя головна ціль?" (вибір одного) |
| 3 | "Скільки часу готовий приділяти?" (5/15/30 хв) |
| 4 | "Готовий до діагностики?" + кнопка "Почати" |

**Збереження:** DataStore (UserPreferences)

---

#### 4.3.2 Diagnostic Screen

**Мета:** Оцінити поточний рівень користувача.

**4 завдання з записом аудіо:**

| # | Завдання | Час | Що аналізується |
|---|----------|-----|-----------------|
| 1 | Читання тексту | 90 сек | Дикція, темп, паузи |
| 2 | Спонтанне мовлення | 60 сек | Структура, паразити, плавність |
| 3 | Емоційне читання | 60 сек | Інтонація, виразність |
| 4 | Переконлива промова | 60 сек | Впевненість, аргументація |

**Результат:** `DiagnosticResult` з оцінками по 7 параметрах (0-100).

---

#### 4.3.3 Home Screen

**Мета:** Центральний хаб, персоналізований план на день.

**Секції:**

1. **Header** — Привітання + streak
2. **Сьогоднішній план** — картки з рекомендованими активностями
3. **Швидкі дії** — кнопки до основних розділів
4. **Прогрес тижня** — міні-графік

**FAB:** Кнопка "AI-тренер" (відкриває AiCoachScreen)

---

#### 4.3.4 Warmup Screen

**Мета:** Щоденна розминка мовленнєвого апарату.

**БЕЗ AI-аналізу аудіо!**

**3 підрозділи:**

| Розділ | Кількість вправ | Час | Механіка |
|--------|-----------------|-----|----------|
| Артикуляційна гімнастика | 12 | 3 хв | Відео + таймер + чекліст |
| Дихальні вправи | 8 | 2 хв | Анімація + таймер + вібрація |
| Розминка голосу | 6 | 2 хв | Аудіо-приклад + повторення |

**+ Швидка розминка** — комбінація найважливіших вправ (5 хв)

---

#### 4.3.5 Courses Screen

**Мета:** Каталог тематичних курсів.

**6 курсів:**

| # | Назва | Уроків | Фокус |
|---|-------|--------|-------|
| 1 | Чітке мовлення за 21 день | 21 | Дикція, скоромовки |
| 2 | Магія інтонації | 21 | Емоції, виразність |
| 3 | Впевнений спікер | 21 | Публічні виступи |
| 4 | Чисте мовлення | 14 | Слова-паразити |
| 5 | Ділова комунікація | 20 | Переговори, співбесіди |
| 6 | Харизматичний оратор | 21 | Просунутий рівень |

---

#### 4.3.6 Lesson Screen

**Мета:** Універсальний екран уроку з вправами.

**Структура уроку:**

1. **Теорія** (1-2 хв) — текст або відео
2. **Вправи** (3-5 штук) — з AI-аналізом
3. **Практичне завдання** — фінальний запис

**Типи вправ:**
- `TONGUE_TWISTER` — скоромовка
- `READING` — читання тексту
- `EMOTION_READING` — читання з емоцією
- `FREE_SPEECH` — вільне мовлення
- `RETELLING` — переказ

---

#### 4.3.7 Improvisation Screen

**Мета:** Тренування спонтанного мовлення.

**4 режими:**

| Режим | Механіка |
|-------|----------|
| Випадкова тема | Тема → 15 сек підготовка → запис 1-3 хв → AI-аналіз |
| Storytelling | Елементи сюжету → розповідь → AI-аналіз |
| Дебати з AI | Тема + позиція → раунди аргументів → AI-контраргументи |
| Продаж | Товар + клієнт → pitch → AI грає клієнта з питаннями |

**+ Щоденний челендж** — унікальне завдання кожен день

---

#### 4.3.8 AI Coach Screen

**Мета:** Персональний чат з AI-тренером.

**Функції:**
- Відповіді на питання про мовлення
- Підготовка до конкретних виступів
- Аналіз завантажених записів
- Персональні поради на основі прогресу
- Симуляції (співбесіда, переговори)

**Контекст:** AI знає профіль користувача, його прогрес, слабкі місця.

---

#### 4.3.9 Progress Screen

**Мета:** Візуалізація прогресу та мотивація.

**Секції:**
1. **Загальний рівень** — прогрес-бар 0-100
2. **Streak** — днів поспіль
3. **Графіки навичок** — 7 параметрів у динаміці
4. **Порівняння "до/після"** — записи з діагностики vs поточні
5. **Досягнення** — бейджі та нагороди

---

## 5. Domain моделі

### 5.1 Користувач

```kotlin
// UserProfile.kt
data class UserProfile(
    val id: String,
    val name: String?,
    val goal: UserGoal,
    val dailyMinutes: Int,
    val createdAt: Long,
    val isPremium: Boolean = false
)

enum class UserGoal {
    CLEAR_SPEECH,      // Говорити чіткіше
    PUBLIC_SPEAKING,   // Виступати впевнено
    BETTER_VOICE,      // Покращити голос
    PERSUASION,        // Переконувати
    INTERVIEW_PREP,    // Підготовка до співбесіди
    GENERAL            // Загальний розвиток
}

// DiagnosticResult.kt
data class DiagnosticResult(
    val id: String,
    val userId: String,
    val timestamp: Long,
    val diction: Int,           // 0-100
    val tempo: Int,             // 0-100
    val intonation: Int,        // 0-100
    val volume: Int,            // 0-100
    val structure: Int,         // 0-100
    val confidence: Int,        // 0-100
    val fillerWords: Int,       // 0-100 (100 = немає паразитів)
    val recordingIds: List<String>,
    val recommendations: List<String>
)

// UserProgress.kt
data class UserProgress(
    val userId: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalMinutes: Int,
    val totalExercises: Int,
    val lastActivityDate: Long,
    val skillLevels: Map<SkillType, Int>,
    val achievements: List<String>
)

enum class SkillType {
    DICTION, TEMPO, INTONATION, VOLUME, STRUCTURE, CONFIDENCE, FILLER_WORDS
}
```

### 5.2 Курси та уроки

```kotlin
// Course.kt
data class Course(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val lessons: List<Lesson>,
    val isPremium: Boolean,
    val estimatedDays: Int,
    val difficulty: Difficulty,
    val skills: List<SkillType>
)

enum class Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }

// Lesson.kt
data class Lesson(
    val id: String,
    val courseId: String,
    val dayNumber: Int,
    val title: String,
    val description: String,
    val theory: TheoryContent?,
    val exercises: List<Exercise>,
    val estimatedMinutes: Int
)

data class TheoryContent(
    val text: String?,
    val videoUrl: String?,
    val tips: List<String>
)

// LessonProgress.kt
data class LessonProgress(
    val lessonId: String,
    val courseId: String,
    val userId: String,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val exerciseResults: Map<String, ExerciseResult>,
    val bestScore: Int?
)
```

### 5.3 Вправи

```kotlin
// Exercise.kt
data class Exercise(
    val id: String,
    val type: ExerciseType,
    val title: String,
    val instruction: String,
    val content: ExerciseContent,
    val durationSeconds: Int,
    val targetMetrics: List<SkillType>
)

enum class ExerciseType {
    TONGUE_TWISTER,     // Скоромовка
    READING,            // Читання тексту
    EMOTION_READING,    // Читання з емоцією
    FREE_SPEECH,        // Вільне мовлення на тему
    RETELLING,          // Переказ
    DIALOGUE,           // Читання діалогу
    PITCH,              // Презентація/pitch
    QA                  // Відповіді на питання
}

sealed class ExerciseContent {
    data class TongueTwister(
        val text: String,
        val difficulty: Int,       // 1-5
        val targetSounds: List<String>
    ) : ExerciseContent()
    
    data class ReadingText(
        val text: String,
        val emotion: Emotion? = null
    ) : ExerciseContent()
    
    data class FreeSpeechTopic(
        val topic: String,
        val hints: List<String>
    ) : ExerciseContent()
    
    data class Dialogue(
        val lines: List<DialogueLine>
    ) : ExerciseContent()
}

enum class Emotion {
    NEUTRAL, JOY, SADNESS, ANGER, SURPRISE, FEAR
}
```

### 5.4 Розминка

```kotlin
// WarmupExercise.kt
data class WarmupExercise(
    val id: String,
    val category: WarmupCategory,
    val title: String,
    val description: String,
    val durationSeconds: Int,
    val repetitions: Int?,
    val mediaType: WarmupMediaType,
    val mediaUrl: String?,
    val animationType: AnimationType?
)

enum class WarmupCategory {
    ARTICULATION,   // Артикуляційна гімнастика
    BREATHING,      // Дихальні вправи
    VOICE           // Розминка голосу
}

enum class WarmupMediaType {
    VIDEO,          // Для артикуляції
    ANIMATION,      // Для дихання
    AUDIO           // Для голосу
}

enum class AnimationType {
    BREATHING_CIRCLE,    // Коло що розширюється/стискається
    BREATHING_SQUARE,    // Квадратне дихання
    TIMER_COUNTDOWN      // Просто таймер
}
```

### 5.5 Імпровізація

```kotlin
// ImprovisationTask.kt
sealed class ImprovisationTask {
    data class RandomTopic(
        val topic: String,
        val difficulty: Difficulty,
        val preparationSeconds: Int,
        val speakingSeconds: Int,
        val hints: List<String>
    ) : ImprovisationTask()
    
    data class Storytelling(
        val format: StoryFormat,
        val elements: StoryElements?
    ) : ImprovisationTask()
    
    data class Debate(
        val topic: String,
        val userPosition: DebatePosition,
        val rounds: Int
    ) : ImprovisationTask()
    
    data class SalesPitch(
        val product: Product,
        val customer: CustomerProfile,
        val pitchSeconds: Int
    ) : ImprovisationTask()
}

enum class StoryFormat {
    WITH_PROMPTS,       // Історія з підказками
    FROM_IMAGE,         // За картинкою
    CONTINUE,           // Продовж історію
    RANDOM_WORDS        // Включи 3 слова
}

data class StoryElements(
    val hero: String?,
    val place: String?,
    val item: String?,
    val twist: String?
)

enum class DebatePosition { FOR, AGAINST }

data class Product(
    val name: String,
    val isAbsurd: Boolean    // Для креативної практики
)

data class CustomerProfile(
    val type: String,        // "зайнятий бізнесмен", "скептик"
    val objections: List<String>
)
```

### 5.6 Аналіз

```kotlin
// VoiceAnalysis.kt
data class VoiceAnalysis(
    val id: String,
    val recordingId: String,
    val timestamp: Long,
    val transcription: String?,
    val metrics: AnalysisMetrics,
    val feedback: AiFeedback,
    val wordsPerMinute: Int?,
    val fillerWordsCount: Map<String, Int>?
)

data class AnalysisMetrics(
    val diction: MetricScore,
    val tempo: MetricScore,
    val intonation: MetricScore,
    val volume: MetricScore,
    val structure: MetricScore?,
    val confidence: MetricScore?,
    val overall: Int    // 0-100
)

data class MetricScore(
    val value: Int,     // 0-100
    val label: String,  // "Добре", "Середньо", "Потребує покращення"
    val details: String?
)

data class AiFeedback(
    val summary: String,
    val strengths: List<String>,
    val improvements: List<String>,
    val tip: String
)
```

### 5.7 Досягнення

```kotlin
// Achievement.kt
data class Achievement(
    val id: String,
    val type: AchievementType,
    val title: String,
    val description: String,
    val iconRes: Int,
    val unlockedAt: Long?,
    val progress: Int?,      // Для прогресивних досягнень
    val target: Int?         // Цільове значення
)

enum class AchievementType {
    // Streak
    STREAK_7, STREAK_30, STREAK_100,
    
    // Курси
    FIRST_COURSE, THREE_COURSES, ALL_COURSES,
    
    // Навички
    DICTION_90, TEMPO_MASTER, EMOTION_ACTOR, ZERO_FILLERS,
    
    // Імпровізація
    IMPROVISER_50, DEBATER_20, SALESMAN_30, STORYTELLER_20,
    
    // Особливі
    EARLY_BIRD, NIGHT_OWL, BREAKTHROUGH
}
```

---

## 6. Фази розробки

### Фаза 0: Підготовка (існуючий проект)

| Блок | Задача | Статус |
|------|--------|--------|
| 0.1 | Оновити структуру пакетів | ⬜ |
| 0.2 | Оновити навігацію (NavRoutes) | ⬜ |
| 0.3 | Додати Room Database | ⬜ |
| 0.4 | Додати DataStore | ⬜ |
| 0.5 | Оновити domain моделі | ⬜ |
| 0.6 | Базові UI компоненти | ⬜ |

### Фаза 1: Онбординг + Діагностика

| Блок | Задача | Статус |
|------|--------|--------|
| 1.1 | Splash Screen | ⬜ |
| 1.2 | Onboarding Screen (Pager) | ⬜ |
| 1.3 | Diagnostic Screen (4 завдання) | ⬜ |
| 1.4 | DiagnosticResult Screen | ⬜ |
| 1.5 | Gemini інтеграція для аналізу | ⬜ |

### Фаза 2: Розминка

| Блок | Задача | Статус |
|------|--------|--------|
| 2.1 | Warmup Screen (головний) | ⬜ |
| 2.2 | Articulation Screen | ⬜ |
| 2.3 | Breathing Screen | ⬜ |
| 2.4 | Voice Warmup Screen | ⬜ |
| 2.5 | Quick Warmup Screen | ⬜ |
| 2.6 | Контент розминки (дані) | ⬜ |

### Фаза 3: Головний екран та навігація

| Блок | Задача | Статус |
|------|--------|--------|
| 3.1 | Home Screen | ⬜ |
| 3.2 | Bottom Navigation | ⬜ |
| 3.3 | Персоналізований план дня | ⬜ |

### Фаза 4: Курси

| Блок | Задача | Статус |
|------|--------|--------|
| 4.1 | Courses List Screen | ⬜ |
| 4.2 | Course Detail Screen | ⬜ |
| 4.3 | Lesson Screen (універсальний) | ⬜ |
| 4.4 | Exercise компоненти | ⬜ |
| 4.5 | Results Screen | ⬜ |
| 4.6 | Контент курсу 1 "Чітке мовлення" | ⬜ |

### Фаза 5: Імпровізація

| Блок | Задача | Статус |
|------|--------|--------|
| 5.1 | Improvisation Screen (головний) | ⬜ |
| 5.2 | Random Topic Screen | ⬜ |
| 5.3 | Storytelling Screen | ⬜ |
| 5.4 | Debate Screen | ⬜ |
| 5.5 | Sales Pitch Screen | ⬜ |
| 5.6 | Daily Challenge | ⬜ |

### Фаза 6: AI-тренер

| Блок | Задача | Статус |
|------|--------|--------|
| 6.1 | AI Coach Screen (чат) | ⬜ |
| 6.2 | Системний промпт | ⬜ |
| 6.3 | Контекст користувача | ⬜ |
| 6.4 | Швидкі дії | ⬜ |

### Фаза 7: Прогрес та гейміфікація

| Блок | Задача | Статус |
|------|--------|--------|
| 7.1 | Progress Screen | ⬜ |
| 7.2 | Compare Screen ("до/після") | ⬜ |
| 7.3 | Achievements Screen | ⬜ |
| 7.4 | Streak логіка | ⬜ |
| 7.5 | Система досягнень | ⬜ |

### Фаза 8: Контент

| Блок | Задача | Статус |
|------|--------|--------|
| 8.1 | Скоромовки (100+) | ⬜ |
| 8.2 | Тексти для читання | ⬜ |
| 8.3 | Теми для імпровізації | ⬜ |
| 8.4 | Теми для дебатів | ⬜ |
| 8.5 | Товари для продажу | ⬜ |
| 8.6 | Контент курсів 2-6 | ⬜ |

### Фаза 9: Freemium та Polish

| Блок | Задача | Статус |
|------|--------|--------|
| 9.1 | Paywall Screen | ⬜ |
| 9.2 | Обмеження для Free | ⬜ |
| 9.3 | In-App Purchases | ⬜ |
| 9.4 | Нотифікації | ⬜ |
| 9.5 | Фінальне полірування | ⬜ |

---

## 7. Freemium модель

### 7.1 Безкоштовний доступ

| Функція | Ліміт |
|---------|-------|
| Онбординг + Діагностика | 1 раз |
| Розминка | Повний доступ |
| Курси | Перші 7 уроків кожного |
| Імпровізація | 3 сесії на день |
| AI-тренер | 10 повідомлень на день |
| Прогрес | Базова статистика |

### 7.2 Premium

| Функція | Доступ |
|---------|--------|
| Повторна діагностика | Необмежено |
| Всі уроки всіх курсів | Повний доступ |
| Імпровізація | Необмежено |
| AI-тренер | Необмежено |
| Прогрес | Повна аналітика + порівняння |
| Персональний план | Так |
| Офлайн-режим | Так |
| Без реклами | Так |

### 7.3 Ціни

- Місячна: $9.99
- Річна: $59.99 (економія 50%)
- Довічна: $149.99

---

## 8. AI інтеграція

### 8.1 Gemini API використання

| Функція | Модель | Токенів ~на запит |
|---------|--------|-------------------|
| Аналіз аудіо після вправи | Flash-Lite | 800 |
| AI-тренер (чат) | Flash-Lite | 1200 |
| Дебати (раунд) | Flash | 1100 |
| Діагностика (повна) | Flash | 3500 |

### 8.2 Системні промпти

#### Для аналізу вправ:

```
Ти — професійний тренер з мовлення. Проаналізуй запис користувача.

Контекст:
- Тип вправи: {exerciseType}
- Очікуваний текст: {expectedText}
- Транскрипція: {transcription}

Оціни за шкалою 0-100:
- Чіткість дикції
- Темп мовлення
- Інтонація (якщо релевантно)

Дай короткий фідбек:
- 1-2 сильні сторони
- 1-2 зони для покращення
- 1 конкретна порада

Відповідь у форматі JSON.
```

#### Для AI-тренера:

```
Ти — AI-тренер з мовлення та публічних виступів в застосунку "AI VoicePower".

Профіль користувача:
- Ціль: {userGoal}
- Поточний рівень: {skillLevels}
- Слабкі місця: {weaknesses}
- Прогрес: {progress}

Твої задачі:
- Відповідати на питання про мовлення
- Допомагати готуватися до виступів
- Давати персоналізовані поради
- Мотивувати користувача

Стиль: дружній, підтримуючий, конкретний.
Мова: українська.
```

#### Для дебатів:

```
Ти — опонент у дебатах на тему: {topic}
Твоя позиція: {oppositePosition}

Користувач щойно навів аргумент:
{userArgument}

Твоя задача:
1. Визнати частково правильні моменти
2. Навести контраргумент
3. Поставити 1 уточнюючe питання

Стиль: поважний, логічний, без агресії.
Довжина: 2-3 речення.
```

### 8.3 Оптимізація витрат

1. **Кешування** — системні промпти кешувати
2. **Вибір моделі** — Flash-Lite для простих задач
3. **Батчинг** — групувати запити де можливо
4. **Ліміти** — обмеження для Free користувачів

---

## 9. Дизайн система

### 9.1 Кольори

```kotlin
// Primary
val Primary = Color(0xFF6366F1)        // Indigo
val PrimaryDark = Color(0xFF4F46E5)
val PrimaryLight = Color(0xFF818CF8)

// Secondary
val Secondary = Color(0xFF8B5CF6)      // Purple
val SecondaryDark = Color(0xFF7C3AED)

// Accent
val Accent = Color(0xFF06B6D4)         // Cyan

// Semantic
val Success = Color(0xFF10B981)        // Green
val Warning = Color(0xFFF59E0B)        // Amber
val Error = Color(0xFFEF4444)          // Red
val Info = Color(0xFF3B82F6)           // Blue

// Neutrals
val Background = Color(0xFFF8FAFC)
val Surface = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF1E293B)
val OnSurfaceVariant = Color(0xFF64748B)
```

### 9.2 Типографіка

```kotlin
val Typography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)
```

### 9.3 Відступи та розміри

```kotlin
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

object CornerRadius {
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val full = 100.dp
}
```

---

## 📝 Примітки для Claude Code

### Загальні правила:

1. **Використовуй існуючу архітектуру** — MVVM + Clean Architecture
2. **Hilt для DI** — всі залежності через @Inject
3. **Compose** — без XML layouts
4. **Flow** — для реактивних даних
5. **Coroutines** — для асинхронності

### При створенні нових екранів:

```kotlin
// Шаблон Screen
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel(),
    onNavigate: (NavRoutes) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    ExampleContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigate = onNavigate
    )
}

@Composable
private fun ExampleContent(
    state: ExampleState,
    onEvent: (ExampleEvent) -> Unit,
    onNavigate: (NavRoutes) -> Unit
) {
    // UI here
}
```

### При створенні ViewModel:

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val repository: ExampleRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ExampleState())
    val state: StateFlow<ExampleState> = _state.asStateFlow()
    
    fun onEvent(event: ExampleEvent) {
        when (event) {
            // handle events
        }
    }
}
```

---

## 🔗 Корисні посилання

- [Google Gemini API](https://ai.google.dev/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Android Speech Recognition](https://developer.android.com/reference/android/speech/SpeechRecognizer)

---

**Останнє оновлення:** Грудень 2024

**Версія:** 2.0
