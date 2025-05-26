package net.pengcook.android.data.model.profile

data class UserProfileResponse(
    val id: Long,
    val email: String,
    val username: String,
    val nickname: String,
    val image: String,
    val region: String,
    val introduction: String?,
    val follower: Int,
    val following: Int,
    val recipeCount: Int,
)
