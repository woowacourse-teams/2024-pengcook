package net.pengcook.android.domain.model.profile

data class UserProfile(
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
    val isFollow: Boolean,
)
