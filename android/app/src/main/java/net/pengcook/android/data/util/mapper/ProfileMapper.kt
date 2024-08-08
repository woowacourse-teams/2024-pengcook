package net.pengcook.android.data.util.mapper

import net.pengcook.android.data.model.profile.UserProfileResponse
import net.pengcook.android.domain.model.profile.UserProfile

fun UserProfileResponse.toUserProfile(): UserProfile =
    UserProfile(
        id = id,
        email = email,
        username = username,
        nickname = nickname,
        image = image,
        region = region,
        introduction = introduction,
        follower = follower,
        following = following,
        recipeCount = recipeCount,
    )
