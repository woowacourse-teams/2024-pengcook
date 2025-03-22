package net.pengcook.android.domain.model.usercontrol

data class FollowInfo(
    val follows: List<FollowUserInfo>,
    val followCount: Long,
)
