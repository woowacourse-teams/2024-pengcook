package net.pengcook.android.presentation.follow

sealed interface FollowListAction {
    data object NavigateBack : FollowListAction

    data class OnTabSelected(val index: Int) : FollowListAction

    data class ShowDialog(val userId: Long) : FollowListAction

    data object HideDialog : FollowListAction

    data class OnDeleteFollower(val userId: Long) : FollowListAction

    data class OnUnfollow(val userId: Long) : FollowListAction
}
