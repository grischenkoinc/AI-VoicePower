package com.aivoicepower.ui.screens.diagnostic

sealed class DiagnosticEvent {
    data class TaskClicked(val taskIndex: Int) : DiagnosticEvent()
    object InstructionDialogDismissed : DiagnosticEvent()
    object StartRecordingClicked : DiagnosticEvent()
    object StopRecordingClicked : DiagnosticEvent()
    data class RecordingTick(val seconds: Int) : DiagnosticEvent()
    object RetakeRecordingClicked : DiagnosticEvent()
    object SaveRecordingClicked : DiagnosticEvent()
    object PreviewDialogDismissed : DiagnosticEvent()
    object CompleteDiagnosticClicked : DiagnosticEvent()
}
