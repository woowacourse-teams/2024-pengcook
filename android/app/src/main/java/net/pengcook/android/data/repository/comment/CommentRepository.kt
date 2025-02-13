package net.pengcook.android.data.repository.comment

import net.pengcook.android.presentation.core.model.Comment
import net.pengcook.android.presentation.core.model.MyComment

interface CommentRepository {
    suspend fun fetchComments(recipeId: Long): Result<List<Comment>>

    suspend fun fetchMyComments(): Result<List<MyComment>>

    suspend fun postComment(
        recipeId: Long,
        message: String,
    ): Result<Unit>

    suspend fun deleteComment(commentId: Long): Result<Unit>
}
