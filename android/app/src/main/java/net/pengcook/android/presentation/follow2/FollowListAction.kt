package net.pengcook.android.presentation.follow2

sealed interface FollowListAction {
    data object NavigateBack : FollowListAction

    data class OnTabSelected(val index: Int) : FollowListAction

    data class ShowDialog(val userId: Long) : FollowListAction

    data object HideDialog : FollowListAction

    data class OnRemoveFollower(val userId: Long) : FollowListAction

    data class OnUnfollow(val userId: Long) : FollowListAction
}
