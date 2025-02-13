package net.pengcook.android.presentation.core.model

data class MyComment(
    val commentId: Long,
    val recipeId: Long,
    val recipeTitle: String,
    val recipeImage: String,
    val createdAt: String,
    val message: String,
)
