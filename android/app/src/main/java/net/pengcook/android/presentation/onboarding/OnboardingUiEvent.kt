package net.pengcook.android.presentation.onboarding

sealed interface OnboardingUiEvent {
    data class NavigateToSignUp(val platformName: String) : OnboardingUiEvent

    data object NavigateToMain : OnboardingUiEvent

    data object Error : OnboardingUiEvent
}
