package net.pengcook.android.presentation.making

sealed interface MakingEvent {
    data object NavigateToMakingStep : MakingEvent

    data object AddImage : MakingEvent
}
