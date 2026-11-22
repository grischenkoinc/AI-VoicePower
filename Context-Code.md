# Context-Code.md — архітектура та стек

## Стек

- Kotlin **2.2.20** (K2), Jetpack Compose з окремим **Compose Compiler Gradle plugin** (`org.jetbrains.kotlin.plugin.compose`) — НЕ старий `composeOptions.kotlinCompilerExtensionVersion`.
- Hilt **2.58** (DI). ⚠️ Hilt 2.59+ вимагає AGP 9.0+ — не оновлювати Hilt без одночасного плану міграції AGP.
- Room **2.8.5**.
- Google Play Billing **8.1.0** (`billing-ktx`). Billing 9.x потребує Kotlin-метадані 2.3, поки не переходимо.
- `compileSdk` / `targetSdk` = **36** (Android 16), `minSdk` = 26. AGP 8.13.1.
- Firebase: Auth, Firestore, Storage, Analytics, Crashlytics, Messaging.
- AdMob + UMP (Google User Messaging Platform) для GDPR consent.
- Gemini API (`gemini-2.5-flash-lite`) для аналізу мовлення — ключ у `local.properties` → `BuildConfig`.

**[2026-09-19/20]** Уся ця версійна база (Kotlin/Hilt/Room/Billing) оновлена одним ланцюжком через вимогу Google Play (targetSdk 36 + Billing 8, дедлайн 31.08.2026, вже прострочений на момент оновлення). Деталі ланцюжка залежностей і чому саме ці версії — у git-коміті `bc5f985` та README цього чату; коротко: Billing 8 вимагав новішого Kotlin (метадані), новіший Kotlin вимагав новішого Hilt і Room (їхні kapt-рідери метаданих не читали Kotlin 2.2), Kotlin 2.0+ вимагає окремий Compose Compiler plugin замість старого способу.

## Ключові файли

- `AiPrompts.kt` — ВСІ AI-промпти централізовано (19 типів вправ + AI-відповідь + Coach). Деталі → [`Context-VoiceAnalysis.md`](Context-VoiceAnalysis.md).
- `GeminiApiClient.kt` — виклики Gemini API, `maxOutputTokens = 2000`.
- `VoiceAnalysisRepositoryImpl.kt` — другий, паралельний шлях аналізу (інший JSON-формат, не змішувати з `GeminiApiClient`).
- `CourseContentProvider.kt` + `data/content/courses/*.kt` — контент курсів → [`Context-Courses.md`](Context-Courses.md).
- `BillingClientWrapper.kt` — обгортка над Google Play Billing. **[2026-09-19]** `queryProductDetailsAsync` callback у Billing 8 повертає `QueryProductDetailsResult` замість голого списку — виправлено (`productDetailsResult.productDetailsList`).
- `AuthRepositoryImpl.kt` / `GoogleSignInHelper.kt` — Firebase Auth (Google + Email/Password).
- `FirebaseModule.kt`, `BillingModule.kt`, `AudioModule.kt`, `DatabaseModule.kt` та ін. — Hilt DI модулі.
- `NotificationHelper.kt` (`utils/`) — локальні сповіщення: канал `coach_reminders`, `showCoachNotification()`, планування через WorkManager (`scheduleCoachReminder`/`cancelCoachReminder`). `CoachReminderWorker.kt` (`data/worker/`) — щоденний `CoroutineWorker`, читає прогрес/streak і показує мотиваційне сповіщення. `service/VoicePowerMessagingService.kt` — приймач push (FCM). Деталі стану push → розділ нижче.

## Навігація

- Splash → Onboarding (сторінка 0 = Welcome) → Auth Screen → Onboarding (сторінка 1+) → Diagnostic (4 завдання із записом голосу + Gemini-аналіз) → Main.
- Auth екран: Google Sign-In, Email/Password. ⚠️ `onSkip`/`showSkipButton` — мертві параметри, кнопки "Пропустити" НЕМАЄ (перевірено емулятором 2026-09-20), хоч стара памʼять про це стверджувала інше.
- **Bottom Navigation**: 5 вкладок — Головна, Курси, Розминка, Impro (імпровізація), Прогрес.
- Settings — іконка "⚙️" у `HomeHeader`, реалізована через `pointerInput`/`detectTapGestures` (НЕ семантичний `.clickable`, тому не видно у деяких accessibility-based tools).

## Технічні "готчі"

- `replace_all` в Edit-тулі замінює ВСІ входження, включно з визначеннями функцій — обережно.
- Два формати JSON аналізу (`VoiceAnalysisResult` основний vs `VoiceAnalysis` в repo-шляху) — не змішувати.
- Мова застосунку — виключно українська (жодної РУ-локалізації UI).
- `google-services.json` — додається вручну з Firebase Console, не в git.
- Room DB — деструктивна міграція увімкнена (версія БД зараз вища за 11, версія Room 2.8.5).
- `isPremium` дефолтить у `false` всюди; є один тестовий акаунт з увімкненим преміум через Firestore.
- AdMob App ID/Ad Unit ID — **реальні продакшн-значення** (застосунок живий у Google Play).
- **Заборона glassmorphism** (напівпрозорі білі оверлеї/frosted glass) — юзер явно заборонив. `AuthScreen.kt` досі порушує це (не виправлено, лише позначено).
- **[2026-09-23]** Версія застосунку, видима юзеру (`AboutScreen.kt`, `SettingsScreen.kt` — subtitle і footer), більше НЕ хардкодиться — прив'язана до `BuildConfig.VERSION_NAME`. До цього рядки типу `"Версія 1.2.5"` розсинхронізовувались із реальним `versionName` в `build.gradle.kts` через кілька релізів підряд (на момент фіксу показували "1.2.5", хоча реліз уже був 1.2.7). Якщо десь у новому UI знову з'явиться хардкоджений рядок версії — це регрес, виправляти на `BuildConfig.VERSION_NAME`.

## Конвенції коду (щоб новий код не відрізнявся стилем)

- **MVI-лайт патерн скрізь**: кожен екран — трійка `XScreen.kt` (Compose) + `XViewModel.kt` (`@HiltViewModel`, приватний `MutableStateFlow(XState())`, публічний `val state: StateFlow<XState>`) + `XEvent` (sealed interface/class) з єдиним входом `fun onEvent(event: XEvent)`. Приклади: `AuthViewModel`/`AuthState`/`AuthEvent`, `LessonViewModel`/`LessonState`/`LessonEvent`. Новий екран — той самий скелет, не вигадувати інший стиль стану.
- **Шари**: `domain/` (моделі, репозиторії-інтерфейси), `data/` (реалізації репозиторіїв, Room, Firebase, мережа), `ui/screens/<feature>/` (Screen + ViewModel + State + Event, підпапка `components/` для дрібних Composable). Слідувати цій структурі для нових фіч.
- **DI через Hilt**: конструктор-injection всюди, модулі в `di/` (`FirebaseModule`, `BillingModule`, `AudioModule`, `DatabaseModule` та ін.) — по одному модулю на групу залежностей, не змішувати.
- **Кастомні тап-жести замість `.clickable`**: там, де потрібна 3D/скейл-анімація на натисканні (напр. іконка Settings у `HomeHeader`), використовують `Modifier.pointerInput(Unit) { detectTapGestures(...) }` замість семантичного `.clickable` — має значення для accessibility/тестування (не видно у деяких tooling), але це усвідомлений стильовий вибір у проєкті, не помилка.
- **Мова коментарів/UI-тексту** — виключно українська (код/імена змінних — англійською, як завжди в Kotlin).

## Push-сповіщення (стан на 2026-11-18)

- **Локальні нагадування** — повністю робочі: `NotificationHelper.kt` + `CoachReminderWorker.kt` (WorkManager), тумблер "Нагадування" в `SettingsScreen.kt`. **[2026-09-21]** Додано runtime-запит `POST_NOTIFICATIONS` (Android 13+) при увімкненні тумблера — за патерном `RECORD_AUDIO` з `AiCoachScreen.kt` (`rememberLauncherForActivityResult` + перевірка перед запуском).
- **Ручні push-розсилки через Firebase Console** — **[2026-09-21] доведено до пуття**: `VoicePowerMessagingService.kt` (приймач FCM, `onMessageReceived`) існував у коді, але ніколи не був закомічений у git — тепер закомічено. Додано в `AndroidManifest.xml` meta-data `default_notification_channel_id`/`default_notification_icon`/`default_notification_color` — без цього push у фоні/закритому застосунку обробляється системою напряму (не через `onMessageReceived`) і йшов би в дефолтний канал без чіткої іконки. Токен FCM реєструється в Firebase автоматично при встановленні застосунку — Console-кампанії таргетяться "на весь застосунок" без зберігання токенів самостійно.
- **[2026-09-23] Знайдено й виправлено живим тестуванням на реальному пристрої (Redmi Note 12, HyperOS/Android 15) — два реальні баги, обидва перевірені візуально на девайсі:**
  1. **`POST_NOTIFICATIONS` майже ніхто не отримував.** Дозвіл запитувався лише глибоко в Налаштуваннях (тумблер "Нагадування"), тому переважна більшість реальних юзерів на Android 13+ його ніколи не бачила — це вбивало і локальні нагадування, і будь-яку push-розсилку для більшості аудиторії. **Виправлено**: одноразовий "м'який" оверлей-запит на Home-екрані (`ui/screens/home/components/NotificationPermissionPrompt.kt`, підключений через `HomeState.showNotificationPrompt` / `HomeEvent.NotificationPromptDismissed` / `HomeViewModel.checkNotificationPromptVisibility()` + новий DataStore-прапорець `hasSeenNotificationPrompt`). Показується один раз — і новим, і вже існуючим юзерам однаково (перевіряється при кожному запуску `HomeViewModel`, не привʼязано до онбордингу) — доки дозвіл не надано і прапорець не встановлено.
  2. **Іконка сповіщення падала в дефолтну "заглушку" на коричневому фоні.** `NotificationHelper.showCoachNotification()` використовував `ic_launcher_foreground` — повноцінну кольорову іконку застосунку — як `setSmallIcon()`, а Android вимагає для статус-бару плаский білий силует на прозорому фоні. **Виправлено**: новий `res/drawable/ic_stat_notification.xml` (монохромна іконка мікрофона) + `setColor(0xFF667EEA)` (фірмовий колір) в `NotificationHelper.kt`, і той самий ресурс/колір (`@color/notification_accent`) у FCM manifest meta-data.
- **[2026-11-16] Переписано тексти `CoachReminderWorker`** (не заливалось, лише компіляція): сегментація за днями з `lastActivityDate` (новачок / був учора зі серією 2+ / був учора / 2–3 / 4–7 / 8+ днів), досягнення (10+ вправ, рекорд серії), загальні тексти про фічі (скоромовка, Impro, AI-коуч, «Без запинок»). Якщо юзер **вже тренувався сьогодні — сповіщення не показується**. Звертання на ім'я опційне, українська множина (день/дні/днів, вправа/вправи/вправ). Текст обирається випадково з пулу відповідних.
- **[2026-11-18] Відновлення розкладу при старті**: `VoicePowerApp.ensureCoachReminderScheduled()` — якщо `isReminderEnabled`, викликає `NotificationHelper.scheduleCoachReminder(..., replaceExisting = false)` → `ExistingPeriodicWorkPolicy.KEEP` (не скидає існуючий розклад; ставить заново, якщо завдання "з'їла" оптимізація батареї Xiaomi/Huawei чи force-stop). Тумблер у Налаштуваннях, як і раніше, використовує `CANCEL_AND_REENQUEUE`.
- **Готчі локальних нагадувань**: `isReminderEnabled` за замовчуванням `false` → нагадування отримують лише ті, хто сам увімкнув тумблер. Час завжди 9:00 — поле `reminderHour` в DataStore є, але `SettingsViewModel` не передає його в `scheduleCoachReminder()` (вибору часу в UI немає). Періодичний WorkManager не точний до хвилини. Немає аналітики показів/відкриттів нагадувань.
- **[2026-11-18] Рішення юзера: автовмикання локальних нагадувань НЕ робимо** (пропонувалось — вмикати при наданні дозволу на Home + одноразова міграція з прапорцем). Основний канал повернення юзерів — **ручні/повторювані кампанії Firebase Console** (сегменти за "Last app engagement", готові тексти → [`Context-Marketing.md`](Context-Marketing.md)). Для більшості юзерів Android 13+ вони запрацюють лише після релізу 1.2.9+ (там запит дозволу на Home).
- ⚠️ **У Firebase Console для тесту використовувати ЛИШЕ кнопку "Send test message"** (конкретний токен одного пристрою, з logcat/тег `FirebaseMessaging`, або тимчасовим `Log.d` в `VoicePowerApp.onCreate()` — швидший спосіб дістати токен, не забути відкотити) — **НІКОЛИ** "Review"/"Publish" з таргетом "весь застосунок" для тестування, бо в застосунку вже є реальні платні користувачі.
- **Відкладено свідомо** (немає конкретного сценарію): адресні/автоматичні розсилки — збереження FCM-токена в `users/{uid}.fcmToken` через Firestore, `@AndroidEntryPoint` на `VoicePowerMessagingService`, Cloud Functions для тригерів — вимагають переходу на платний план Firebase (Blaze), Cloud Functions у проєкті ще не налаштовувались.

## Нагороди: анкета + реферали — технічні рішення [2026-11-22]

План і чекбокси → [`REWARDS_ROADMAP.md`](REWARDS_ROADMAP.md). Ключове:
- **Бонусний баланс** (`bonus_balance/{uid}`: 10 аналізів + 3 Impro за анкету) захищається **лише Firestore rules, без Cloud Functions**: create тільки за наявності `feedback/{uid}` і рівно 10/3, update тільки −1, delete заборонено. Порядок витрачання: Pro → щоденні безкоштовні → бонус → вікно ліміту; під час Pro бонус не списується.
- **Pro-дні за рефералів** видає лише сервер (Cloud Functions `redeemReferralCode`/`activateReferral`, поле `users/{uid}.proUntil`) → потрібен тариф **Blaze**. Активація — після завершення онбордингу, 1 нагорода на пристрій, акаунт < 72 год, ліміт 7 днів/міс.
- ⚠️ Знайдено ризик: `CloudSyncRepositoryImpl.kt:63` записує `isPremium = true` у Firestore **з клієнта** — якщо rules дозволяють юзеру писати свій документ, Pro можна видати собі вручну. Закрити в Етапі 2 без поломки наявних підписників. Файлу `firestore.rules` у репозиторії немає — rules живуть лише в Firebase Console.
- Rewarded-кнопку в `AnalysisLimitBottomSheet.kt` ховаємо прапорцем `ads_enabled` (Remote Config), `RewardedAdManager` лишається.

## ⚠️ Другий, окремий рівень premium-гейтингу уроків (важливо, легко переплутати)

**[2026-09-23]** `Course.isPremium` (в `CourseContentProvider.kt`/окремих `*Course.kt`) контролює лише те, чи курс показується заблокованим у **списку курсів**. Це НЕ визначає, чи весь контент курсу безкоштовний — навіть якщо `isPremium = false` (як в `IntonationMagicCourse`), окремі уроки всередині курсу можуть бути заблоковані окремим, глобальним правилом.

Реальний гейт — `getLessonLockReason()` в `app/src/main/java/com/aivoicepower/ui/screens/courses/CourseDetailState.kt` (рядки ~26-63), викликається з `CourseDetailViewModel.kt` і `LessonViewModel.findNextLesson()`. Правило **однакове для всіх курсів, без винятків**: уроки 1-3 завжди відкриті, 4-7 відкриваються прогресивно, а **індекс уроку ≥7 (тобто день 8+) вимагає Premium-підписки юзера, незалежно від `Course.isPremium`**. Додано навмисно самим юзером (коміти `8a8c0a0`, `403469c`, "Progressive lesson unlocking" / "Pro sequential lessons").

Бейдж у UI називається **"PRO"** (не "Premium") — `LessonListItem.kt`, `CourseDetailScreen.kt`, `PremiumSettingsCard.kt`.

⚠️ Є ще третя, схожа, але **не підключена до UI** реалізація — `PremiumChecker.canAccessLesson()`/`getRemainingFreeLessons()` (`utils/PremiumChecker.kt`) з тим самим лімітом 7 через `FreeTierLimits.FREE_LESSONS_PER_COURSE`, але жодна ViewModel її не викликає для гейтингу уроків (лише AI-analysis-лімітні методи `PremiumChecker` реально використовуються). Ймовірно мертвий/дублюючий код — не плутати з реальним гейтом вище.

**Висновок для будь-якої задачі "цей курс безкоштовний/платний":** перевіряти `getLessonLockReason()`, а не лише `Course.isPremium` — інакше висновок буде неповним чи неправильним (сталось у цій самій сесії).

## Тестування на реальному пристрої через adb (без USB)

- Wireless debugging (Android 11+) — `adb pair <ip>:<port> <code>` (код парування можна передати як другий аргумент, без інтерактивного стдіна), потім `adb connect <ip>:<port>` з **головного** екрану Wireless debugging (інший порт, ніж у парування). З'єднання може відвалюватись (Wi-Fi сон/таймаут) — тоді повторити `adb pair` зі свіжим кодом (коди живуть коротко, ~1 хв).
- **Redmi Note 12 / HyperOS (MIUI-based) блокує через wireless-adb shell**: `pm grant`/`pm revoke` runtime-дозволів (`SecurityException: ...GRANT_RUNTIME_PERMISSIONS`) і `input tap` (`SecurityException: ...INJECT_EVENTS`) — обидва потребують ручної дії на самому пристрої (тумблер у системних Налаштуваннях, реальний тап пальцем). Скрін і логи (`adb shell screencap`, `adb logcat`) працюють нормально.
- На Xiaomi/HyperOS активація Developer options іноді пропонує "режим підприємства" замість Developer options — відомий баг, лікується очищенням кешу застосунків "Безпека" та "Налаштування" (не пов'язано з кодом застосунку).
- `adb pull` з шляхів на пристрої (`/sdcard/...`) у Git Bash на Windows треба писати з подвійним слешем (`adb pull //sdcard/file.png ./local.png`) — інакше MSYS конвертує `/sdcard/...` у `C:/Program Files/Git/sdcard/...` і провалюється.

## Перевірено емулятором (2026-09-19/20, Android 16 / API 36)

Повний прохід: онбординг → реєстрація (Firebase Auth) → діагностика (4 записи мікрофоном + реальний виклик Gemini API, JSON розпарсено коректно) → головний екран → курси (７ курсів, 140 уроків підвантажились коректно) → paywall (реальні ціни з Google Play: $14.99/$39.99/$99.99). Жодного крашу за всю сесію. `assembleDebug`/`assembleRelease`/`bundleRelease` — усі `BUILD SUCCESSFUL`.
