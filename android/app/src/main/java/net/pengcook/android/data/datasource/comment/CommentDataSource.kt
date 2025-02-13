package net.pengcook.android.data.datasource.comment

import net.pengcook.android.data.model.comment.CommentRequest
import net.pengcook.android.data.model.comment.CommentResponse
import net.pengcook.android.data.model.comment.MyCommentResponse
import retrofit2.Response

interface CommentDataSource {
    suspend fun fetchComment(
        accessToken: String,
        recipeId: Long,
    ): Response<List<CommentResponse>>

    suspend fun fetchMyComments(accessToken: String): Response<List<MyCommentResponse>>

    suspend fun postComment(
        accessToken: String,
        commentRequest: CommentRequest,
    ): Response<Unit>

    suspend fun deleteComment(
        accessToken: String,
        commentId: Long,
    ): Response<Unit>
}
