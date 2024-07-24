package net.pengcook.android.presentation.signup

import java.io.File

sealed interface SignUpEvent {
    class NavigateToMain(
        val accessToken: String,
        val refreshToken: String,
    ) : SignUpEvent

    data object Error : SignUpEvent

    data object NavigateBack : SignUpEvent
}
