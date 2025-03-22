package net.pengcook.android.data.model.usercontrol

data class FollowDataResponse(
    val follows: List<FollowerInfo>,
    val followCount: Long,
)
