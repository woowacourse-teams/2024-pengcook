package net.pengcook.android.presentation.onboarding

sealed interface OnboardingUiEvent {
    data object StartSignIn : OnboardingUiEvent

    data class NavigateToSignUp(val platformName: String) : OnboardingUiEvent

    data object NavigateToMain : OnboardingUiEvent

    data object Error : OnboardingUiEvent
}
