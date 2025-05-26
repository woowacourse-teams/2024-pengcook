package net.pengcook.android.presentation.profile.model

data class Profile(
    val username: String,
    val nickname: String,
    val imageUrl: String,
    val followers: Long,
    val recipeCount: Long,
    val introduction: String = "",
)
