package net.pengcook.android.presentation.core.model

data class Feed(
    val id: Long,
    val username: String,
    val profileImageUrl: String,
    val recipeImageUrl: String,
    val recipeTitle: String,
    val likeCount: Int,
    val commentCount: Int,
)
