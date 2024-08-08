package net.pengcook.android.data.repository.comment

import net.pengcook.android.presentation.core.model.Comment

interface CommentRepository {
    suspend fun fetchComments(recipeId: Long): Result<List<Comment>>

    suspend fun postComment(
        recipeId: Long,
        message: String,
    ): Result<Unit>
}
