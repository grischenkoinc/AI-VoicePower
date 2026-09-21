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

## Конвенції коду (щоб новий код не відрізнявся стилем)

- **MVI-лайт патерн скрізь**: кожен екран — трійка `XScreen.kt` (Compose) + `XViewModel.kt` (`@HiltViewModel`, приватний `MutableStateFlow(XState())`, публічний `val state: StateFlow<XState>`) + `XEvent` (sealed interface/class) з єдиним входом `fun onEvent(event: XEvent)`. Приклади: `AuthViewModel`/`AuthState`/`AuthEvent`, `LessonViewModel`/`LessonState`/`LessonEvent`. Новий екран — той самий скелет, не вигадувати інший стиль стану.
- **Шари**: `domain/` (моделі, репозиторії-інтерфейси), `data/` (реалізації репозиторіїв, Room, Firebase, мережа), `ui/screens/<feature>/` (Screen + ViewModel + State + Event, підпапка `components/` для дрібних Composable). Слідувати цій структурі для нових фіч.
- **DI через Hilt**: конструктор-injection всюди, модулі в `di/` (`FirebaseModule`, `BillingModule`, `AudioModule`, `DatabaseModule` та ін.) — по одному модулю на групу залежностей, не змішувати.
- **Кастомні тап-жести замість `.clickable`**: там, де потрібна 3D/скейл-анімація на натисканні (напр. іконка Settings у `HomeHeader`), використовують `Modifier.pointerInput(Unit) { detectTapGestures(...) }` замість семантичного `.clickable` — має значення для accessibility/тестування (не видно у деяких tooling), але це усвідомлений стильовий вибір у проєкті, не помилка.
- **Мова коментарів/UI-тексту** — виключно українська (код/імена змінних — англійською, як завжди в Kotlin).

## Перевірено емулятором (2026-09-19/20, Android 16 / API 36)

Повний прохід: онбординг → реєстрація (Firebase Auth) → діагностика (4 записи мікрофоном + реальний виклик Gemini API, JSON розпарсено коректно) → головний екран → курси (７ курсів, 140 уроків підвантажились коректно) → paywall (реальні ціни з Google Play: $14.99/$39.99/$99.99). Жодного крашу за всю сесію. `assembleDebug`/`assembleRelease`/`bundleRelease` — усі `BUILD SUCCESSFUL`.
