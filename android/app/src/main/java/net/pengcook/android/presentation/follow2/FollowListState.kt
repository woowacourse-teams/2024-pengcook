package net.pengcook.android.presentation.follow2

import net.pengcook.android.presentation.follow2.model.FollowInfo

data class FollowListState(
    val userId: Long = 0L,
    val username: String = "",
    val selectedTabIndex: Int = 0,
    val searchQuery: String = "",
    val followers: List<FollowInfo> = emptyList(),
    val followerCount: Long = 0L,
    val followings: List<FollowInfo> = emptyList(),
    val followingCount: Long = 0L,
    val isLoading: Boolean = true,
    val isMine: Boolean = false,
    val showDialog: Boolean = false,
    val selectedFollowInfo: FollowInfo? = null,
)
