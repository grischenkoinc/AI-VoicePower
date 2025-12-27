Виправ 2 проблеми:

=== БАГ 1: "setAudioSource failed" ===

Це помилка дозволів мікрофона! Перевір:

1. Чи є дозвіл в AndroidManifest.xml?

grep -n "RECORD_AUDIO\|MICROPHONE" app/src/main/AndroidManifest.xml

Якщо немає — додай:
<uses-permission android:name="android.permission.RECORD_AUDIO" />

2. Чи запитується runtime permission?

grep -rn "RECORD_AUDIO\|requestPermission\|permission" app/src/main/java/com/aivoicepower/ui/screens/diagnostic/

Потрібно запитувати дозвіл перед записом:

val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // Почати запис
        viewModel.onEvent(DiagnosticEvent.StartRecording)
    } else {
        // Показати повідомлення про необхідність дозволу
    }
}

// При натисканні кнопки:
onClick = {
    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
}

3. Перевір AudioRecorderUtil — чи правильно налаштований MediaRecorder?

grep -n "setAudioSource\|MediaRecorder" app/src/main/java/com/aivoicepower/utils/audio/AudioRecorderUtil.kt

=== БАГ 2: Анімація кнопок ===

Додай простішу анімацію через InteractionSource:

@Composable
fun AnimatedPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "buttonScale"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Text(text)
    }
}

Застосуй до кнопок "Почати" та "Почати запис" в DiagnosticScreen.kt

Imports потрібні:
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer

=== КРОК 3: Компіляція ===

./gradlew assembleDebug

=== РЕЗУЛЬТАТ ===

Покажи:
1. Чи був дозвіл RECORD_AUDIO в Manifest?
2. Чи є runtime permission request?
3. Як виправив помилку запису
4. Чи додав анімації
5. Результат компіляції