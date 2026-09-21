# Context-VoiceAnalysis.md — аналіз мовлення, промпти, оцінки

> ⚠️ **Стиль/конвенції написання нових AI-промптів тут ще не задокументовані** (свідомо відкладено 2026-09-21 — зараз немає активної задачі на нові промпти). Якщо новий чат матиме задачу написати/змінити промпт в `AiPrompts.kt` — спершу прочитати кілька існуючих `buildXPrompt` функцій для стилю, і за бажанням доповнити цей файл конвенціями за тим самим принципом, що зроблено для `Context-Courses.md`.

## Два паралельні шляхи аналізу (не змішувати!)

- **`GeminiApiClient.analyzeVoiceRecording()`** — формат `VoiceAnalysisResult`. Основний шлях.
- **`VoiceAnalysisRepositoryImpl.analyzeVoice()`** — формат `VoiceAnalysis`. Другий, паралельний шлях, окремі промпти.

Це два різні JSON-формати відповіді від Gemini — код, який очікує один формат, зламається на іншому.

## Промпти

- `AiPrompts.kt` — **єдине джерело правди** для всіх AI-промптів: 19 промптів під конкретні типи вправ + окремий промпт для генерації AI-відповіді + промпти AI-коуча (`DEFAULT_QUICK_ACTIONS` в `AiCoachRepositoryImpl.kt`).
- 19 типів вправ (спільні для курсів і Impro-розділу):
  1. Reading (Читання) | 2. Tongue Twister (Скоромовка) | 3. Slow Motion | 4. Minimal Pairs
  5. Emotional Reading | 6. Free Speech | 7. Storytelling | 8. Debate
  9. Sales | 10. Job Interview | 11. Presentation | 12. Negotiation
  13. Retelling | 14. Dialogue | 15. No Hesitation | 16. Metaphor Master
  17. Emotion Switch | 18. Speed Round | 19. Character Voice
- `GeminiApiClient` — `maxOutputTokens = 2000` (було 800, спричиняло обрізані відповіді для української).
- Модель: `gemini-2.5-flash-lite`.

## Система оцінок

Перевірено наживо (діагностика, емулятор 2026-09-19): Gemini повертає JSON з полями типу `diction`, `tempo`, `intonation`, `volume`, `confidence`, `fillerWords`, `structure`, `persuasiveness` (окремі поля можуть бути `null`, якщо запис закороткий/нерелевантний для цього виміру), плюс `overallScore` (0-100), `divergences` (що розійшлось з очікуваним текстом), `strengths` (масив рядків), `improvements` (масив рядків), `tip` (порада), `coachComment` (репліка AI-коуча). Формат обгорнутий у \`\`\`json блок у відповіді моделі.

Приклад реального результату (тестовий прогін): 4 короткі тестові записи → підсумкова оцінка 17/100 з розбивкою по категоріях (Дикція 41, Темп 36, Інтонація 30, Структура/Впевненість/Без слів-паразитів — 0, бо записи були занадто короткі для цих вимірів).

## Ліміти безкоштовного тарифу (стосується Impro теж)

- `FreeTierLimits.kt` — 5 аналізів/день + 3 за рекламу; окремо `freeImprovAnalysesToday` + `freeAdImprovToday` для Impro-розділу (1+1). `FREE_RECORDING=60s`, `PRO_RECORDING=120s`.
- `PremiumChecker.kt` — `canAnalyzeExercise()`, `canAnalyzeImprovisation()`, `getRemainingAnalyses()`.
- `AnalysisLimitBottomSheet.kt` — 3 варіанти при вичерпанні ліміту: подивитись рекламу / Premium / продовжити без аналізу.
- `ServerLimitService.kt` — серверні ліміти у Firestore (`daily_limits/{userId}`), захист від зміни дати на пристрої.
- `SkillUpdateService.normalizeExerciseType()` — мапить типи вправ Impro-розділу на ключі skill-мапи.

## AI Коуч

- `AiCoachRepositoryImpl.kt` — окрема логіка чат-коуча, використовує `AiPrompts.DEFAULT_QUICK_ACTIONS`.
- На головному екрані є картка з AI-коучем ("QA, привіт! Я — твій AI-тренер з мовлення...") — генерується на основі денного плану/прогресу.

## Аналітика по аналізу (Firebase)

- `AnalyticsTracker.kt` — єдиний клас для всіх Firebase Analytics подій.
- `GeminiApiClient` трекає токени через `response.usageMetadata` (promptTokenCount, candidatesTokenCount).
- Події: `exercise_started`, `recording_completed`, `ai_analysis_completed`, `gemini_generation`, `coach_message_sent`, `limit_reached` та ін. Повний список і план дашбордів → `ANALYTICS_ROADMAP.md`.
