# Analytics & Back-Office — RoadMap

## ЕТАП 1: ДО РЕЛІЗУ НА PLAY MARKET
> Мета: почати збирати дані з першого дня тестування. Без цього — дані втрачені назавжди.

---

### 1.1 Firebase Analytics — базова аналітика (безкоштовно)
**Що це:** SDK від Google, який автоматично збирає дані про користувачів (DAU, retention, екрани, сесії) + дозволяє логувати свої кастомні події.

**Що дає тобі:** бачиш у Firebase Console скільки юзерів, які екрани дивляться, де відвалюються, Free vs Pro.

**Що робимо:**
- [x] Додати залежність `firebase-analytics-ktx` в `build.gradle.kts`
- [x] Створити `AnalyticsTracker.kt` — єдиний клас для всіх подій
- [x] Встановити User Properties: `is_premium`, `account_type` (google/email/anonymous)
- [x] Логувати custom events:

| Event | Коли спрацьовує | Навіщо |
|-------|-----------------|--------|
| `exercise_started` | Юзер починає будь-яку вправу | Які вправи популярні |
| `recording_completed` | Завершив запис голосу | Скільки записують, яка тривалість |
| `ai_analysis_requested` | Натиснув "Аналізувати" | Скільки аналізів = скільки Gemini коштує |
| `ai_analysis_completed` | Gemini повернув результат | Точні токени (input/output), latency |
| `gemini_generation` | Gemini згенерував текст для TTS (чат, дебати тощо) | Окрема категорія витрат — текст для озвучення |
| `tts_synthesized` | TTS озвучив текст | Скільки символів = скільки TTS коштує |
| `coach_message_sent` | Юзер написав у AI Coach | Навантаження на чат |
| `ad_watched` | Юзер подивився рекламу для аналізу | Монетизація |
| `limit_reached` | Юзер вичерпав безкоштовні ліміти | Потенційні конверсії в Pro |
| `onboarding_step` | Кожен крок онбордінгу | Де відвалюються |
| `auth_action` | Реєстрація/логін/скіп | Конверсія реєстрації |

---

### 1.2 Firebase Crashlytics — звіти про крейші (безкоштовно)
**Що це:** Автоматично ловить крейші та помилки додатку, показує стектрейс, пристрій, версію.

**Що дає тобі:** бачиш ВСІ крейші тестувальників в реальному часі замість "у мене щось не працює".

**Що робимо:**
- [x] Додати залежність `firebase-crashlytics-ktx` + gradle plugin
- [x] Додати `crashlytics` plugin в `build.gradle.kts`
- [x] Готово — працює автоматично, нічого більше писати не потрібно

---

### 1.3 Трекінг Gemini токенів — розуміння витрат на AI
**Що це:** Gemini SDK повертає `usageMetadata` з кожною відповіддю — точна кількість токенів (input + output).

**Що дає тобі:** точно знаєш скільки коштує кожен аналіз та кожна генерація тексту для TTS.

**Що робимо:**
- [x] В `GeminiApiClient.kt` — дістати `response.usageMetadata` після кожного виклику
- [x] Передати дані в `AnalyticsTracker` з контекстом (analysis / debate / coach / тощо)
- [x] Окремо логувати: analysis токени vs generation-for-TTS токени

**3 категорії витрат Gemini:**
```
A. Аналіз записів (analyzeVoiceRecording, analyzeImprovisationExercise)
   → дорого по input (аудіо), результат = JSON оцінка

B. Генерація для інтерактивних вправ (debate, sales, interview, presentation, negotiation)
   → результат = текст → йде в TTS → подвійна витрата (Gemini + TTS)

C. AI Coach (generateCoachResponse, generateQuickActions)
   → результат = текст → йде в TTS → подвійна витрата (Gemini + TTS)
```

---

### 1.4 Трекінг TTS символів — розуміння витрат на озвучення
**Що це:** Google Cloud TTS рахує по символах. Chirp3-HD ≈ $16 за 1M символів.

**Що дає тобі:** знаєш скільки символів синтезовано і скільки це коштує.

**Що робимо:**
- [x] В `CloudTtsManager.kt` — після кожного synthesis логувати `char_count` + `context`
- [x] Передати в `AnalyticsTracker`

---

### 1.5 BigQuery Export — увімкнути збір сирих даних
**Що це:** Firebase автоматично вивантажує ВСІ події в BigQuery (Google-овська база для аналітики). Потім по них можна робити SQL-запити та графіки.

**Що дає тобі:** сирі дані для будь-яких звітів. Основа для dashboard-ів пізніше.

**Що робимо:**
- [x] Увімкнути в Firebase Console → Project Settings → Integrations → BigQuery → Link
- [x] Це все. Далі дані збираються автоматично.

---

## ЕТАП 2: ПІСЛЯ РЕЛІЗУ (під час або після 2-тижневого тестування)
> Мета: побудувати інструменти для аналізу зібраних даних та управління юзерами.

---

### 2.1 Looker Studio Dashboard — візуалізація всіх даних (безкоштовно)
**Що це:** Google-овський інструмент для побудови dashboard-ів. Підключається до BigQuery.

**Що дає тобі:** красиві графіки та таблиці замість сирих даних.

**Що робимо:**
- [ ] Підключити Looker Studio до BigQuery
- [ ] Створити dashboard-и:
  - **Витрати AI:** хвилини аналізу, токени, estimated cost, по типах вправ, Free vs Pro
  - **Витрати TTS:** символи, estimated cost, по контексту
  - **Користувачі:** DAU/MAU, retention, Free vs Pro ratio, воронка
  - **Монетизація:** реклама, ліміти, конверсії

---

### 2.2 Cloud Functions — серверна логіка (безкоштовно до 2M викликів/місяць)
**Що це:** Код на сервері Google, який запускається автоматично при подіях (новий юзер, новий запис) або по розкладу.

**Що дає тобі:** автоматизація + HTTP API для адмін-панелі.

**Що робимо:**
- [ ] Firestore triggers: при новому аналізі → оновити агреговану статистику
- [ ] Auth triggers: при реєстрації юзера → створити запис в базі юзерів
- [ ] Scheduled: щоденне очищення старих записів (якщо додамо cloud storage)
- [ ] HTTP endpoints для адмін-панелі (list users, grant premium, view stats)

---

### 2.3 Адмін-панель — управління юзерами
**Що це:** Веб-інтерфейс для тебе, де бачиш список юзерів, їх активність, можеш видати Premium тощо.

**Варіанти:**
- **Appsmith self-hosted** — безкоштовно, необмежено, повний функціонал
- **Retool** — безкоштовно до 5 адмінів

**Що робимо:**
- [ ] Розгорнути Appsmith (або Retool)
- [ ] Підключити до Cloud Functions HTTP API
- [ ] Побудувати:
  - Таблиця юзерів з пошуком/фільтрами
  - Картка юзера (активність, вправи, витрати)
  - Дії: Grant Premium, Block, Reset Limits
  - Зведена статистика

---

### 2.4 Firestore Analytics Collection — real-time метрики
**Що це:** Окрема колекція в Firestore з агрегованими даними по днях. Firebase Analytics має затримку 24-48 год, а це — в реальному часі.

**Що дає тобі:** миттєво бачиш сьогоднішні витрати та активність.

**Що робимо:**
- [ ] Колекція `analytics_daily/{date}` з summary + розбивкою по типах вправ + по юзерах
- [ ] Cloud Function оновлює при кожному аналізі/TTS виклику

---

### 2.5 Cloud Storage для діагностик
**Що це:** Зберігання записів діагностик та контрольних вправ у Firebase Storage.

**Що дає тобі:** юзер не втратить ключові записи, може порівняти прогрес.

**Що робимо:**
- [ ] Завантажувати в хмару тільки: онбордінг-діагностику, повторні діагностики, контрольні вправи курсу
- [ ] Cloud Function: автоматичне очищення записів старших 90 днів (опціонально)
- [ ] Все інше — залишається локально

---

## Витрати (прогноз)

| Компонент | Вартість |
|-----------|----------|
| Firebase Analytics | Безкоштовно |
| Crashlytics | Безкоштовно |
| BigQuery (перший 1TB запитів/міс) | Безкоштовно |
| Looker Studio | Безкоштовно |
| Cloud Functions (перші 2M/міс) | Безкоштовно |
| Appsmith self-hosted | Безкоштовно |
| Firebase Storage (діагностики) | ~$1-3/міс на 1K юзерів |
| **Gemini API** | **~$0.50-2 за 1000 аналізів** |
| **Google Cloud TTS** | **~$16 за 1M символів** |

---

## Файли які будуть змінені/створені (Етап 1)

| Файл | Дія |
|------|-----|
| `app/build.gradle.kts` | Додати firebase-analytics, crashlytics залежності |
| `build.gradle.kts` (project) | Додати crashlytics gradle plugin |
| **`AnalyticsTracker.kt`** (новий) | Єдиний клас для логування всіх подій |
| `GeminiApiClient.kt` | Дістати usageMetadata, передати в AnalyticsTracker |
| `CloudTtsManager.kt` | Логувати char_count після synthesis |
| ViewModels (Lesson, RandomTopic, Storytelling, DailyChallenge, AiCoach, TongueTwisters) | Логувати exercise_started, recording_completed, limit_reached |
| `MainActivity.kt` або `Application` class | Ініціалізація Analytics User Properties |
| `AuthRepositoryImpl.kt` | Логувати auth_action |
| Onboarding screens | Логувати onboarding_step |

---

## Верифікація

1. Зібрати проект → перевірити що компілюється без помилок
2. Firebase Console → Analytics → DebugView → перевірити що події приходять
3. Firebase Console → Crashlytics → перевірити що з'являється в dashboard
4. Logcat → фільтр "Analytics" → перевірити що events логуються
