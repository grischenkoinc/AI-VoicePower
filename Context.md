# Context.md — AI VoicePower / Diqto

> Хаб-файл. Тримати коротким. Деталі — у тематичних `Context-*.md` та наявних доках. Оновлюється навичкою `/handoff`, завантажується навичкою `/context`.
>
> **[2026-09-21]** Уся ця система контексту створена й закомічена (`2f1984c`) — усі `Context-*.md`, крім `Context-Business.md` (локально, у `.gitignore`), тепер під git.

## Що це за проєкт

Android-застосунок для тренування дикції/мовлення з AI-коучем (Gemini). Пакет `com.aivoicepower`. **Публічна назва в Google Play — "Diqto — тренуй дикцію з АІ"**, відрізняється від назви репозиторію/внутрішньої назви "AI VoicePower" — часте джерело плутанини, майте на увазі.

Розробник у Play Console: акаунт **Trivium Studio** (login `trivium.studio.dev@gmail.com`). Деталі бізнесу/акаунта → [`Context-Business.md`](Context-Business.md) (лише локально, не в git).

Проєкт був "відкладений на полицю" на 5 місяців (востаннє білдився ~квітень 2026), відновлена робота у вересні 2026 після перших органічних завантажень і першої оплаченої підписки.

## Поточний статус (станом на 2026-09-21)

| Версія | versionCode | Останній публічний реліз |
|---|---|---|
| 1.2.6 | 19 | Вересень 2026, залито в продакшн Play Console |

| Напрямок | Стан | Деталі |
|---|---|---|
| Технічні вимоги Play (targetSdk 36, Billing Library 8) | ✅ Готово | [`Context-Code.md`](Context-Code.md) |
| Опис застосунку в Play Market (емодзі, переклад РУ) | ✅ Подано, на перевірці Google | [`Context-Business.md`](Context-Business.md) |
| Податкова інформація / ФОП / виплати | 🔄 В процесі — чекаємо довідку від ДПС | [`Context-Business.md`](Context-Business.md) |
| Контент курсів (4 з 7 курсів мають лише 7/21 уроку) | 🔄 IntonationMagic 14/21 написано (2026-09-21), стиль-гайд готовий для решти | [`Context-Courses.md`](Context-Courses.md) |
| Розділ "Impro" (імпровізація) | ℹ️ Оглянуто, повністю робочий | [`Context-Improvisation.md`](Context-Improvisation.md) |
| Конвенції коду (MVI-патерн, шари, DI) | ✅ Задокументовано | [`Context-Code.md`](Context-Code.md) |

## Покажчик документів

| Файл | Коли відкривати |
|---|---|
| [`Context-Code.md`](Context-Code.md) | Загальна архітектура, стек, DI, навігація, технічні "готчі" — відкривати для будь-якої кодової задачі |
| [`Context-VoiceAnalysis.md`](Context-VoiceAnalysis.md) | Логіка аналізу мовлення, промпти Gemini, система оцінок і порад AI-коуча |
| [`Context-Courses.md`](Context-Courses.md) | Стан контенту курсів, які уроки написані/заглушки |
| [`Context-Improvisation.md`](Context-Improvisation.md) | Розділ вільної імпровізації (Debate/Sales/Interview/тощо) |
| [`Context-Business.md`](Context-Business.md) | Play Console акаунт, монетизація, ФОП/податки, ReseraAI — **лише локально, не в git** |
| `README.md` | Загальний опис проєкту для людини (трохи застарів — там ще Android SDK 34) |
| `FIREBASE_SETUP.md` | Налаштування Firebase-проєкту |
| `ANALYTICS_ROADMAP.md` | План аналітики/back-office |
| `PROMPT_IMPROVEMENT_ROADMAP.md` | План покращення AI-промптів |
| `CLAUDE_TASK.md`/`2`/`3` | **Не контекст-доки** — скретч-зона для вставлених нотаток/багрепортів, не чіпати |

## Відомі незакриті дрібниці

- `AuthScreen.kt` має мертві параметри `onSkip`/`showSkipButton` (`@Suppress("UNUSED_PARAMETER")`) — кнопки "Пропустити" насправді немає, хоча раніше вважалось що є.
- **Заборона glassmorphism** (напівпрозорі білі оверлеї, frosted glass) — юзер явно заборонив, але `AuthScreen.kt` досі має напівпрозору картку (не виправлено, лише позначено).
- Короткий опис у Play Market не мав спецсимволів (емодзі прибрано) — якщо повертаєте емодзі, перевірте правило "не рекламується в Google Play" знову.
