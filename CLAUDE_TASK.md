В вашем приложении используются неподдерживаемые API или параметры отображения от края до края
Один или несколько используемых вами параметров для отображения от края до края или API не поддерживаются в Android 15. Параметры и API, задействованные в вашем приложении:

android.view.Window.setStatusBarColor
android.view.Window.setNavigationBarColor
LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
Их расположение:

androidx.activity.u.b
androidx.activity.x.b
com.google.android.material.datepicker.p.onStart
androidx.activity.v.v
Вам нужно отказаться от них, чтобы исправить проблему.