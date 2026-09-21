# Context-Improvisation.md — розділ "Impro"

> ⚠️ **Стиль/конвенції для нових типів вправ тут ще не задокументовані** (свідомо відкладено 2026-09-21 — секція вже повністю реалізована, активної задачі на розширення немає). Якщо з'явиться задача додати новий тип Impro-вправи — варто спершу проаналізувати існуючі 8 типів на консистентність за тим самим принципом, що зроблено для `Context-Courses.md`.

> Досліджено 2026-09-21. Усі шляхи відносно `app/src/main/java/com/aivoicepower/`.

## Структура

Хаб: `ui/screens/improvisation/ImprovisationScreen.kt` + `ImprovisationViewModel.kt` (сітка з 8 карток-режимів). Реєстрація маршрутів — `ui/navigation/Screen.kt` (~рядки 42-53), `MainNavGraph.kt` (~рядки 155, 307-379). Вкладка в нижній навігації — `MainScreen.kt` → `BottomNavItem.Improvisation`.

**Стан: повністю реалізовано, жодних TODO/FIXME/заглушок не знайдено.** Весь пайплайн запис → STT → AI-хід/аналіз → результат — робочий у кожному з 8 екранів.

## Безкоштовні вправи (запис голосу → аналіз Gemini)

- **RandomTopic** — випадкова тема, монолог, голосовий аналіз. `exerciseType = "random_topic"`.
- **Storytelling** — формат історії, запис, аналіз. `exerciseType = "storytelling_<format>"`.
- **DailyChallenge** (`data/provider/DailyChallengeProvider.kt`) — щоденне завдання, що ротується серед 6 типів: OPPOSITE_DAY, METAPHOR_MASTER, NO_HESITATION, EMOTION_SWITCH, SPEED_ROUND, CHARACTER_VOICE.

## Преміум-вправи (діалог з AI по чергах: TTS → запис → STT → наступний хід → фінальний текстовий аналіз)

- **Debate** — дебати за/проти теми з AI-опонентом.
- **SalesPitch** (+ `SalesProductsProvider`) — продаж товару AI-клієнту, заперечення.
- **JobInterview** — імітація HR-співбесіди; у ViewModel захардкожені пули імен HR/компаній/ключових слів (IT/Маркетинг/Фінанси/Загальне).
- **Presentation** — презентація + відповіді на AI-питання аудиторії.
- **Negotiation** — переговори по сценарію проти AI.

Заблоковані повністю на рівні хаб-екрана (`isLocked = !state.isPremium`).

## Ліміти — два незалежні шари

**A. Ліміт входу** (`ImprovisationViewModel.canStartImprovisation()`, `freeImprovisationsToday` vs хардкод `3`) — гейтить навігацію лише для RandomTopic/Storytelling/DailyChallenge.

**B. Ліміт AI-аналізу** (`PremiumChecker.canAnalyzeImprovisation()` + `FreeTierLimits.FREE_IMPROV_ANALYSES_PER_DAY`/`FREE_AD_IMPROV_ANALYSES_PER_DAY`) — викликається **тільки** в RandomTopic/Storytelling/DailyChallenge ViewModels.

**Debate/JobInterview/Negotiation/Presentation/SalesPitch НЕ викликають ні `PremiumChecker`, ні `FreeTierLimits` взагалі** — не потрібно, бо повністю за Premium-стіною на рівні хаба. Але сама Premium-перевірка є **лише в UI хаб-екрана** (`onClick`/`isLocked`), не на рівні nav-графа чи ViewModel — `MainNavGraph.kt` реєструє ці 5 маршрутів як звичайні `composable()` без перевірки. Сьогодні неексплуатабельно (хаб — єдина точка входу), але не defense-in-depth.

## AiPrompts.kt — дві різні підсистеми промптів

- **Безкоштовні голосові вправи** → `AiPrompts.buildVoiceAnalysisPrompt()` — великий `when`-диспетчер за підрядком `exerciseType` (`buildStorytellingPrompt`, `buildDebateAnalysisPrompt`, `buildSalesAnalysisPrompt`, `buildJobInterviewPrompt`, `buildPresentationPrompt`, `buildNegotiationPrompt`, `buildNoHesitationPrompt`, `buildMetaphorMasterPrompt`, `buildEmotionSwitchPrompt`, `buildSpeedRoundPrompt`...). Незматчене падає в `buildFreeSpeechPrompt` — саме туди потрапляє `"random_topic"` і, схоже, **CHARACTER_VOICE** (немає окремої гілки під нього — перевірити, чи це навмисно).
- **Преміум-вправи по ходах** → `GeminiApiClient.analyzeImprovisationExercise()` використовує **загальний** `AiPrompts.buildImprovisationAnalysisPrompt()` для фінальної оцінки (не окремі `buildXAnalysisPrompt`), плюс парні `buildXSystemPrompt`/`buildXUserPrompt` для генерації кожного ходу AI.
  - ⚠️ **Пастка з назвами**: для JobInterview генерація ходів іде через `AiPrompts.buildInterviewSystemPrompt`/`buildInterviewUserPrompt` (без "Job" у назві), а окрема функція `buildJobInterviewPrompt` — це щось інше (гілка голосового диспетчера). Легко переплутати при рефакторингу.

## DI — непослідовність

- RandomTopic/Storytelling/DailyChallenge ViewModels — інжектять `VoiceAnalysisRepository` (абстракція).
- Debate/SalesPitch/JobInterview/Presentation/Negotiation ViewModels — інжектять **`GeminiApiClient` напряму**, без repository-абстракції.

## Дрібні готчі

- `ImprovisationViewModel.kt:62` — `dailyLimit = 3` захардкожено, коментар `// FreeTierLimits.FREE_IMPROVISATIONS_PER_DAY` не підключений (константа існує, просто не використана) — косметично, значення співпадає.
- Усі 5 Premium ViewModels обгортають `incrementFreeImprovisations()` у порожній `try { } catch (_: Exception) {}` — тихе ковтання помилок, однаково скопійовано в усіх 5 файлах.
- Хаб перевіряє `state.isPremium` напряму замість через `PremiumChecker` (на відміну від безкоштовних карток, які йдуть через `viewModel.canStartImprovisation()`) — стилістична непослідовність, не баг.
