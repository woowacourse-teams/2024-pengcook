package net.pengcook.android.presentation.signup

sealed interface SignUpEvent {
    data object SignInSuccessful : SignUpEvent

    data object Error : SignUpEvent

    data object BackPressed : SignUpEvent

    data object SignUpFormNotCompleted : SignUpEvent

    data object UsernameInvalid : SignUpEvent

    data object NicknameLengthInvalid : SignUpEvent

    data object NicknameDuplicated : SignUpEvent
}
