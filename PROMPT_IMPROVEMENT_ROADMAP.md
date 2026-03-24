# Roadmap: Виправлення системи AI промптів

## Контекст

Фідбек тестувальників виявив дві критичні проблеми:
1. Юзер чітко вимовив 5-слівну скоромовку повністю — AI сказав що прочитано лише 87% тексту (хибна транскрипція/порівняння)
2. Юзер повторював ту саму вправу ідентично — отримував різні оцінки та суперечливі поради

**Кореневі причини:**
- **Проблема 1:** `transcribeAudio()` неточно транскрибує коротку українську скоромовку → `calculateCompletionPercent()` вважає що 1 з 5 слів не збігся → 87% → `capScoreByCompletion(87%)` обмежує max score до 65. Також `normalizeToWords()` фільтрує слова `length > 1`, що може відкидати короткі українські слова ("і", "у", "в", "й"). Для коротких текстів (≤10 слів) кожне "непізнане" слово = -20%, тому похибка транскрипції критична.
- **Проблема 2:** temperature=0.8 (занадто високо для оцінювання), відсутність якорів оцінювання, інструкція "90+ рідко!", anchoring bias від прикладів JSON з низькими оцінками, відсутність історії попередніх спроб, deround() додає шум.

---

## ФАЗА 0: Виправлення completion% для коротких текстів
**Файл:** `app/src/main/java/com/aivoicepower/data/repository/VoiceAnalysisRepositoryImpl.kt`
**Статус:** [x] DONE

### Проблема
Для скоромовки з 5 слів: транскрипція пропускає/змінює 1 слово → completion=80% → cap score=65.
Це несправедливо, бо похибка транскрипції Gemini Flash Lite для коротких українських фраз занадто висока.

### Зміни

1. **`normalizeToWords()`** (рядок 302) — змінити фільтр `it.length > 1` на `it.isNotEmpty()`:
   - Зараз відкидає односимвольні слова ("і", "у", "в", "й", "з", "о")
   - Це може зменшити кількість очікуваних слів або транскрибованих, збиваючи completion%

2. **`calculateCompletionPercent()`** (рядок 341) — для коротких текстів (≤10 слів) зробити м'якший підрахунок:
   - Якщо 4 з 5 слів збіглись = 80% зараз, але для 5 слів це "майже все" → вважати 95%
   - Формула: для текстів ≤ 10 слів, якщо пропущено лише 1 слово → completion = max(обчислене, 95)
   - Логіка: одне слово може бути хибно транскрибоване → benefit of the doubt

3. **`capScoreByCompletion()`** (рядок 426) — пом'якшити cap для високих completion%:
   - Зараз: `completionPercent < 100 → max 80` (навіть 99% = cap 80)
   - Змінити: `completionPercent >= 95 → max 100` (не обмежувати), `>= 90 → max 90`, `>= 75 → max 70`
   - Зараз: `< 90 → max 65` — це занадто жорстко

4. **`fuzzyMatch()`** (рядок 314) — додати фонетичну толерантність:
   - Для українських слів додати толерантність до типових помилок транскрипції: г/ґ, і/ї, е/є

---

## ФАЗА 1: Окремий GenerativeModel для аналізу
**Файл:** `app/src/main/java/com/aivoicepower/data/remote/GeminiApiClient.kt`
**Статус:** [x] DONE

1. Додати `analysisGenerativeModel` з параметрами:
   - `temperature = 0.15f`, `topK = 10`, `topP = 0.5f`, `maxOutputTokens = 2000`

2. Використовувати `analysisGenerativeModel` в:
   - `analyzeVoiceRecording()` (рядок 397)
   - `transcribeAudio()` (рядок 482)
   - `analyzeImprovisationExercise()` (рядок 574)

3. Залишити `generativeModel` (temp=0.8) для творчих задач:
   - `generateDebateResponse`, `generateSalesResponse`, `generateInterviewResponse`
   - `generatePresentationResponse`, `generateNegotiationResponse`
   - `generateCoachResponse`, `generateQuickActions`

4. Видалити `breakRounding()` (рядки 538-561) та виклик на рядку 529 — з temperature=0.15 округлення мінімальне, deround додає шум

---

## ФАЗА 2: Виправлення ANALYSIS_HEADER
**Файл:** `app/src/main/java/com/aivoicepower/data/remote/AiPrompts.kt` (рядки 24-44)
**Статус:** [x] DONE

1. **Замінити шкалу** — замість "90+ рідко!, більшість = 35-65":
   ```
   ШКАЛА ОЦІНЮВАННЯ (оцінюй відносно ЦЬОГО конкретного завдання, а не абсолютного рівня професіоналізму):
   90-100: Відмінно — завдання виконано бездоганно, мінімальні зауваження
   75-89: Добре — впевнене виконання з незначними недоліками
   55-74: Задовільно — помітні недоліки, є над чим працювати
   35-54: Слабко — суттєві проблеми, потрібна практика
   0-34: Дуже слабко — завдання не виконано або критичні помилки
   ```

2. **Прибрати** "НЕ завищуй оцінки!"

3. **Додати правило узгодженості:**
   ```
   УЗГОДЖЕНІСТЬ: strengths МУСЯТЬ відповідати високим оцінкам (>=75), improvements — низьким (<=50). Якщо ставиш diction >= 80 — НЕ пиши про проблеми з дикцією в improvements. Якщо ставиш diction <= 40 — НЕ пиши про хорошу дикцію в strengths
   ```

4. **Додати правило для коротких текстів:**
   ```
   Для коротких текстів (до 10 слів) — фокусуйся ТІЛЬКИ на головній метриці. Другорядні метрики (volume, intonation для скоромовки) оцінюй м'якше — не штрафуй за те, що неможливо продемонструвати у 5 словах
   ```

5. **Додати інструкцію для divergences:**
   ```
   Якщо є ОЧІКУВАНИЙ ТЕКСТ — додай "divergences": опиши розбіжності. Формат: "Пропущено: 'X'. Замінено: 'Y' → 'Z'. Додано: 'W'". Якщо все вірно або немає очікуваного тексту: "divergences": null
   ```

6. **Додати інструкцію для reasoning (chain-of-thought):**
   ```
   ПЕРШ НІЖ ставити оцінки — запиши в "reasoning" ЩО ти чуєш: коротко транскрибуй ключові фрагменти, відміть проблеми, порахуй паразити. Потім на основі reasoning — став оцінки. reasoning — ПЕРШЕ поле в JSON
   ```

7. **Додати інструкцію для історії попередніх спроб:**
   ```
   Якщо є ПОПЕРЕДНІ СПРОБИ — порівняй з ними. Якщо юзер покращився — ВІДМІТЬ це в strengths! Не повторюй tip які вже давались раніше. Якщо попередня порада вже виправлена — похвали і дай НАСТУПНИЙ крок розвитку
   ```

8. **Додати інструкцію для рівня користувача:**
   ```
   Якщо вказано РІВЕНЬ КОРИСТУВАЧА — адаптуй складність порад. Початківець: прості конкретні кроки (дихання, артикуляція). Середній: конкретні техніки. Просунутий: нюанси і тонкощі
   ```

---

## ФАЗА 3: Виправлення JSON прикладів у 19 промптах
**Файл:** `app/src/main/java/com/aivoicepower/data/remote/AiPrompts.kt`
**Статус:** [x] DONE

В кожному з 19 `buildXxxPrompt()` + 2 repo-level промптах:
- Замінити конкретні числа на `<0-100>` placeholder
- Замінити `"overallScore": 54` на `"overallScore": <розрахуй за формулою>`
- Додати `"reasoning"` як ПЕРШЕ поле в JSON
- Додати `"divergences"` поле

**Було:**
```json
{
  "diction": 58, "tempo": 52, "intonation": 46, "volume": 68,
  "confidence": null, ...
  "overallScore": 54,
  "strengths": ["(твоє спостереження...)"], ...
}
```

**Стало:**
```json
{
  "reasoning": "(що саме я чую: транскрипція ключових фрагментів, помічені проблеми)",
  "diction": <0-100>, "tempo": <0-100>, "intonation": <0-100>, "volume": <0-100>,
  "confidence": null, ...
  "overallScore": <розрахуй за формулою>,
  "divergences": "<розбіжності з текстом або null>",
  "strengths": ["(твоє спостереження...)"], ...
}
```

**Промпти для зміни (19 + 2):**
1. `buildReadingPrompt` | 2. `buildTongueTwisterPrompt` | 3. `buildSlowMotionPrompt`
4. `buildMinimalPairsPrompt` | 5. `buildEmotionalReadingPrompt` | 6. `buildFreeSpeechPrompt`
7. `buildStorytellingPrompt` | 8. `buildDebateAnalysisPrompt` | 9. `buildSalesAnalysisPrompt`
10. `buildJobInterviewPrompt` | 11. `buildPresentationPrompt` | 12. `buildNegotiationPrompt`
13. `buildRetellingPrompt` | 14. `buildDialoguePrompt` | 15. `buildNoHesitationPrompt`
16. `buildMetaphorMasterPrompt` | 17. `buildEmotionSwitchPrompt` | 18. `buildSpeedRoundPrompt`
19. `buildCharacterVoicePrompt` | 20. `buildTongueTwisterRepoPrompt` | 21. `buildStandardRepoPrompt`

---

## ФАЗА 4: Нові поля в VoiceAnalysisResult + парсинг
**Статус:** [x] DONE

**Файл:** `app/src/main/java/com/aivoicepower/domain/model/VoiceAnalysisResult.kt`
- Додати поля: `val divergences: String? = null`, `val reasoning: String? = null`
- Оновити `default()` з новими полями

**Файл:** `app/src/main/java/com/aivoicepower/data/remote/GeminiApiClient.kt`
- В `VoiceAnalysisJsonResponse` додати: `divergences: String?`, `reasoning: String?`
- В `parseVoiceAnalysisResponse()` додати маппінг нових полів

---

## ФАЗА 5: AnalysisResultEntity — збереження історії аналізів
**Статус:** [x] DONE

### Нові файли:

**`app/src/main/java/com/aivoicepower/data/local/database/entity/AnalysisResultEntity.kt`**
```kotlin
@Entity(tableName = "analysis_results")
data class AnalysisResultEntity(
    @PrimaryKey val id: String,
    val exerciseId: String?,       // конкретна вправа (BaseExercise.id)
    val exerciseType: String,      // тип вправи (normalized: "tongue_twister", "reading"...)
    val overallScore: Int,
    val diction: Int, val tempo: Int, val intonation: Int, val volume: Int,
    val confidence: Int, val fillerWords: Int, val structure: Int, val persuasiveness: Int,
    val tip: String,
    val strengths: String,         // JSON array as string
    val improvements: String,      // JSON array as string
    val divergences: String?,
    val timestamp: Long
)
```

**`app/src/main/java/com/aivoicepower/data/local/database/dao/AnalysisResultDao.kt`**
```kotlin
@Dao
interface AnalysisResultDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(result: AnalysisResultEntity)

    @Query("SELECT * FROM analysis_results WHERE exerciseId = :exerciseId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getByExerciseId(exerciseId: String, limit: Int = 3): List<AnalysisResultEntity>

    @Query("SELECT * FROM analysis_results WHERE exerciseType = :type ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getByExerciseType(type: String, limit: Int = 3): List<AnalysisResultEntity>
}
```

### Зміни в існуючих файлах:

**`app/src/main/java/com/aivoicepower/data/local/database/AppDatabase.kt`**
- Додати `AnalysisResultEntity::class` в entities array
- Додати `abstract fun analysisResultDao(): AnalysisResultDao`
- Версія: 11 → 12 (destructive migration вже увімкнена)

**`app/src/main/java/com/aivoicepower/di/DatabaseModule.kt`**
- Додати provide-метод для `AnalysisResultDao`

**`app/src/main/java/com/aivoicepower/domain/repository/VoiceAnalysisRepository.kt`**
- Додати параметр: `exerciseId: String? = null`

**`app/src/main/java/com/aivoicepower/data/repository/VoiceAnalysisRepositoryImpl.kt`**
- Inject `AnalysisResultDao` та `DiagnosticResultDao` (якщо ще не inject)
- Додати параметр `exerciseId` до `analyzeRecording()`
- **Перед аналізом:**
  - Підтягнути попередні спроби: пріоритет exerciseId (до 3), доповнити exerciseType (до 3)
  - Підтягнути рівень юзера з `DiagnosticResultDao.getAllResultsOnce()`
  - Додати все це в `enrichedContext`
- **Після аналізу:**
  - Зберегти результат в `analysis_results` таблицю

### Оновити виклики в ViewModels (додати exerciseId):

| ViewModel | Файл | exerciseId |
|-----------|-------|-----------|
| LessonViewModel | `:442` | `currentExerciseState.exercise.id` |
| TongueTwistersViewModel | `:293` | `twister?.id` |
| DailyChallengeViewModel | `:284` | `challenge.id` |
| RandomTopicViewModel | `:247` | `null` |
| StorytellingViewModel | `:250` | `null` |

---

## Порядок виконання

| # | Фаза | Що робимо | Вплив | Файли |
|---|------|-----------|-------|-------|
| 1 | Фаза 0 | Виправити completion% для коротких текстів | Фіксить проблему "87% скоромовки" | VoiceAnalysisRepositoryImpl.kt |
| 2 | Фаза 1 | analysisGenerativeModel + видалити deround | Фіксить консистентність оцінок | GeminiApiClient.kt |
| 3 | Фаза 4 | Нові поля reasoning + divergences | Підготовка для промптів | VoiceAnalysisResult.kt, GeminiApiClient.kt |
| 4 | Фаза 2+3 | Виправити ANALYSIS_HEADER + 19 JSON прикладів | Фіксить справедливість оцінок | AiPrompts.kt |
| 5 | Фаза 5 | Entity + DAO + DB + DI + Repository + ViewModels | Історія спроб + рівень юзера | 8+ файлів |

**Фази 0-3** — "quick wins", вирішують обидві проблеми тестувальників негайно, без архітектурних змін.
**Фаза 4-5** — архітектурна зміна для довгострокової якості (послідовний коучинг).

---

## Верифікація

- [ ] **Консистентність:** одна вправа, 5 аналізів поспіль → оцінки ±3-5 балів
- [ ] **Справедливість:** чітка коротка скоромовка → 90+
- [ ] **Узгодженість:** strengths не суперечать improvements
- [ ] **Історія:** повторна вправа → прогрес відмічається, поради не повторюються
- [ ] **Build:** `./gradlew assembleDebug` без помилок
