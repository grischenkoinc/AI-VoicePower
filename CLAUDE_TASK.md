1. В вашем приложении используется версия SDK с примечанием о критической проблеме от reCAPTCHA Enterprise
Разработчик SDK reCAPTCHA Enterprise (com.google.android.recaptcha:recaptcha) добавил к версии 18.1.2 указанное ниже примечание.

The version is deprecated, please upgrade to a non-deprecated version: https://cloud.google.com/recaptcha/docs/deprecation-policy-mobile A critical security vulnerability was discovered in reCAPTCHA Enterprise for Mobile. The vulnerability has been patched in the latest SDK release. Customers will need to update their Android application with the reCAPTCHA Enterprise for Mobile SDK, version 18.4.0 or above. We strongly recommend you update to the latest version as soon as possible.

Затронута следующая версия вашего приложения:
5

-------------

2. Отображение от края до края может работать не у всех пользователей
Начиная с Android 15, показ от края до края будет включен по умолчанию для всех приложений, использующих SDK 35. Чтобы контент приложений выглядел корректно в Android 15 и более новых версиях, разработчики должны будут самостоятельно регулировать отступы. Изучите этот вопрос, протестируйте показ от края до края и внесите необходимые изменения. Чтобы обеспечить обратную совместимость, вы также можете вызвать метод enableEdgeToEdge() в Kotlin или EdgeToEdge.enable() в Java.

------------

3. В вашем приложении используются неподдерживаемые API или параметры отображения от края до края
Один или несколько используемых вами параметров для отображения от края до края или API не поддерживаются в Android 15. Параметры и API, задействованные в вашем приложении:

android.view.Window.setStatusBarColor
android.view.Window.setNavigationBarColor
Их расположение:

androidx.activity.q.a
androidx.activity.r.a
com.aivoicepower.MainActivity.onCreate
com.google.android.material.datepicker.o.onStart
Вам нужно отказаться от них, чтобы исправить проблему.

--------------

4. Удалите из приложения ограничения на изменение размера и ориентации, чтобы оно поддерживало устройства с большим экраном
Начиная с Android 16, Android будет игнорировать ограничения на изменение размера и ориентации на устройствах с большим экраном (например, складных телефонах и планшетах). Из-за этого возможны ошибки с отображением и снижение удобства использования.

Мы обнаружили в вашем приложении следующие ограничения на изменение размера и ориентации:

<activity android:name="com.aivoicepower.MainActivity" android:screenOrientation="PORTRAIT" />
Чтобы сделать приложение удобнее для пользователей, удалите эти ограничения и проверьте корректность его работы на экранах разного размера и с разной ориентацией на Android 16 и более ранних версиях.