package net.pengcook.android.presentation.follow2.model

import net.pengcook.android.domain.model.usercontrol.FollowUserInfo

data class FollowInfo(
    val userId: Long,
    val profileImageUrl: String,
    val username: String,
)

fun FollowUserInfo.toPresentationModel(): FollowInfo = FollowInfo(
    userId = this.userId,
    profileImageUrl = this.profileImage,
    username = this.username,
)
