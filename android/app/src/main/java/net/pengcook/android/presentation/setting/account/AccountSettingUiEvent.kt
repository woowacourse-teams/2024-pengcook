package net.pengcook.android.presentation.setting.account

sealed interface AccountSettingUiEvent {
    data object SignOut : AccountSettingUiEvent

    data object DeleteAccount : AccountSettingUiEvent

    data object NavigateBack : AccountSettingUiEvent
}
