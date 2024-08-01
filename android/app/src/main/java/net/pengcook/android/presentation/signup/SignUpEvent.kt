package net.pengcook.android.presentation.signup

sealed interface SignUpEvent {
    data object NavigateToMain : SignUpEvent

    data object Error : SignUpEvent

    data object NavigateBack : SignUpEvent

    data object UsernameInvalid : SignUpEvent

    data object NicknameLengthInvalid : SignUpEvent

    data object NicknameDuplicated : SignUpEvent
}
