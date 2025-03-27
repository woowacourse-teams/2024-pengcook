package net.pengcook.android.data.model.usercontrol

data class FollowDataResponse(
    val follows: List<FollowerInfoResponse>,
    val followCount: Long,
)
