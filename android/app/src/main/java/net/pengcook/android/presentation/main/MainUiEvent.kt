package net.pengcook.android.presentation.main

sealed interface MainUiEvent {
    data object NavigateToMain : MainUiEvent

    data object NavigateToOnboarding : MainUiEvent
}
