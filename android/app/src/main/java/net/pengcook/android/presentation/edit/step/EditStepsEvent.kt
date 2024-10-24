package net.pengcook.android.presentation.edit.step

sealed interface EditStepsEvent {
    data object NavigationEvent : EditStepsEvent

    data object TempSaveEvent : EditStepsEvent

    data object OnSaveFailure : EditStepsEvent

    data object OnFetchComplete : EditStepsEvent

    data object ExitEvent : EditStepsEvent
}
