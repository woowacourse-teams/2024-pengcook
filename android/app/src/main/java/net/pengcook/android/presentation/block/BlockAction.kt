package net.pengcook.android.presentation.block

sealed interface BlockAction {
    data object NavigateBack : BlockAction

    data class UnblockUser(val blockeeId: Long) : BlockAction
}
