package net.pengcook.android.data.model.profile

data class UserProfileResponse(
    val id: Long,
    val email: String,
    val username: String,
    val nickname: String,
    val image: String,
    val region: String,
    val introduction: String?,
    val followerCount: Int,
    val followingCount: Int,
    val recipeCount: Int,
    val isFollow: Boolean = false,
)
