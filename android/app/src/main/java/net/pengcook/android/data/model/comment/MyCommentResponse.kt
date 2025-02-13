package net.pengcook.android.data.model.comment

data class MyCommentResponse(
    val commentId: Long,
    val recipeId: Long,
    val recipeTitle: String,
    val recipeThumbnail: String,
    val createdAt: String,
    val message: String,
)
