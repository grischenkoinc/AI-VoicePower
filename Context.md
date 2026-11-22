# Context.md — AI VoicePower / Diqto

> Хаб-файл. Тримати коротким. Деталі — у тематичних `Context-*.md` та наявних доках. Оновлюється навичкою `/handoff`, завантажується навичкою `/context`.
>
> **[2026-09-21]** Уся ця система контексту створена й закомічена (`2f1984c`) — усі `Context-*.md`, крім `Context-Business.md` (локально, у `.gitignore`), тепер під git.

## Що це за проєкт

Android-застосунок для тренування дикції/мовлення з AI-коучем (Gemini). Пакет `com.aivoicepower`. **Публічна назва в Google Play — "Diqto — тренуй дикцію з АІ"**, відрізняється від назви репозиторію/внутрішньої назви "AI VoicePower" — часте джерело плутанини, майте на увазі.

Розробник у Play Console: акаунт **Trivium Studio** (login `trivium.studio.dev@gmail.com`). Деталі бізнесу/акаунта → [`Context-Business.md`](Context-Business.md) (лише локально, не в git).

Проєкт був "відкладений на полицю" на 5 місяців (востаннє білдився ~квітень 2026), відновлена робота у вересні 2026 після перших органічних завантажень і першої оплаченої підписки.

## Поточний статус (станом на 2026-09-24)

| Версія | versionCode | Стан |
|---|---|---|
| 1.2.6 | 19 | Останній публічний реліз — вересень 2026, у продакшн Play Console |
| 1.2.9 | 22 | Зібрано локально 2026-09-24 (CharismaticSpeaker 21/21 + push-фікси + прибрано хардкод версії в UI), **ще не залито** в Play Console. (1.2.7/20 і 1.2.8/21 — проміжні локальні збірки, поглинуті 1.2.9, ніколи не заливались) |

| Напрямок | Стан | Деталі |
|---|---|---|
| Технічні вимоги Play (targetSdk 36, Billing Library 8) | ✅ Готово, **перевірено повторно 2026-09-24** (targetSdk=36, billing-ktx=8.1.0 — жодна сесійна зміна цього не торкнулась) | [`Context-Code.md`](Context-Code.md) |
| Опис застосунку в Play Market (емодзі, переклад РУ) | ✅ Подано, на перевірці Google | [`Context-Business.md`](Context-Business.md) |
| Податкова інформація / ФОП / виплати | 🔄 В процесі — чекаємо довідку від ДПС | [`Context-Business.md`](Context-Business.md) |
| Контент курсів (1 з 7 курсів має лише 7/21 уроку) | 🔄 IntonationMagic (09-21), VoicePower (09-22), ConfidentSpeaker (09-23), CharismaticSpeaker (09-24) завершено 21/21 — лишився лише BusinessCommunication | [`Context-Courses.md`](Context-Courses.md) |
| Push-сповіщення | ✅ **[2026-09-23]** Виправлено 2 реальні баги знайдені живим тестом: майже ніхто не отримував `POST_NOTIFICATIONS` (тепер одноразовий запит на Home), іконка падала в дефолтну заглушку (тепер власна монохромна). **[2026-11-18]** Нові сегментовані тексти локальних нагадувань + відновлення розкладу при старті (локально, не залито); автовмикання відхилено — основний канал = кампанії Firebase Console | [`Context-Code.md`](Context-Code.md) |
| Розділ "Impro" (імпровізація) | ℹ️ Оглянуто, повністю робочий | [`Context-Improvisation.md`](Context-Improvisation.md) |
| Нагороди замість rewarded-реклами (анкета + реферали) | 📝 **[2026-11-22]** Сплановано, roadmap з позначками Opus/Sonnet; наступне — Етап 1 (анкета + бонусний баланс, без Blaze) | [`REWARDS_ROADMAP.md`](REWARDS_ROADMAP.md), [`Context-Business.md`](Context-Business.md) |
| Маркетинг / рілси (TT, YT Shorts, Reels через Google Flow) | 🆕 **[2026-10-01]** Напрям створено, обираємо формати | [`Context-Marketing.md`](Context-Marketing.md) |
| Конвенції коду (MVI-патерн, шари, DI) | ✅ Задокументовано | [`Context-Code.md`](Context-Code.md) |

## Покажчик документів

| Файл | Коли відкривати |
|---|---|
| [`Context-Code.md`](Context-Code.md) | Загальна архітектура, стек, DI, навігація, технічні "готчі" — відкривати для будь-якої кодової задачі |
| [`Context-VoiceAnalysis.md`](Context-VoiceAnalysis.md) | Логіка аналізу мовлення, промпти Gemini, система оцінок і порад AI-коуча |
| [`Context-Courses.md`](Context-Courses.md) | Стан контенту курсів, які уроки написані/заглушки |
| [`Context-Improvisation.md`](Context-Improvisation.md) | Розділ вільної імпровізації (Debate/Sales/Interview/тощо) |
| [`Context-Business.md`](Context-Business.md) | Play Console акаунт, монетизація, ФОП/податки, ReseraAI — **лише локально, не в git** |
| [`Context-Marketing.md`](Context-Marketing.md) | Просування: соцмережі, рілси (Google Flow), формати, контент-план |
| `README.md` | Загальний опис проєкту для людини (трохи застарів — там ще Android SDK 34) |
| `FIREBASE_SETUP.md` | Налаштування Firebase-проєкту |
| `ANALYTICS_ROADMAP.md` | План аналітики/back-office |
| `REWARDS_ROADMAP.md` | Анкета + реферальна програма замість rewarded-реклами: кроки з чекбоксами й позначками Opus/Sonnet |
| `PROMPT_IMPROVEMENT_ROADMAP.md` | План покращення AI-промптів |
| `CLAUDE_TASK.md`/`2`/`3` | **Не контекст-доки** — скретч-зона для вставлених нотаток/багрепортів, не чіпати |

## Відомі незакриті дрібниці

- `AuthScreen.kt` має мертві параметри `onSkip`/`showSkipButton` (`@Suppress("UNUSED_PARAMETER")`) — кнопки "Пропустити" насправді немає, хоча раніше вважалось що є.
- **Заборона glassmorphism** (напівпрозорі білі оверлеї, frosted glass) — юзер явно заборонив, але `AuthScreen.kt` досі має напівпрозору картку (не виправлено, лише позначено).
- Короткий опис у Play Market не мав спецсимволів (емодзі прибрано) — якщо повертаєте емодзі, перевірте правило "не рекламується в Google Play" знову.
- **[2026-09-23]** `Course.isPremium = false` НЕ означає "весь курс безкоштовний" — є окреме, глобальне правило `getLessonLockReason()`, яке блокує день 8+ будь-якого курсу за Premium незалежно від цього прапорця. Деталі → [`Context-Code.md`](Context-Code.md).
