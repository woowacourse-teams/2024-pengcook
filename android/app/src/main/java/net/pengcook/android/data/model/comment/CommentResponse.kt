package net.pengcook.android.data.model.comment

data class CommentResponse(
    val commentId: Long,
    val userId: Long,
    val userImage: String,
    val userName: String,
    val createdAt: String,
    val message: String,
    val mine: Boolean,
)
