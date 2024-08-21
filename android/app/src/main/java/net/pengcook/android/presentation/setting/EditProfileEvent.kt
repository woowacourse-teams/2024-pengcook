package net.pengcook.android.presentation.setting

sealed interface EditProfileEvent {
    data object EditProfileSuccessful : EditProfileEvent

    data object AddImage : EditProfileEvent

    data class PresignedUrlRequestSuccessful(val presignedUrl: String) : EditProfileEvent

    data object PostImageSuccessful : EditProfileEvent

    data object PostImageFailure : EditProfileEvent

    data object Error : EditProfileEvent

    data object BackPressed : EditProfileEvent

    data object FormNotCompleted : EditProfileEvent

    data object UsernameInvalid : EditProfileEvent

    data object NicknameLengthInvalid : EditProfileEvent

    data object NicknameDuplicated : EditProfileEvent
}
