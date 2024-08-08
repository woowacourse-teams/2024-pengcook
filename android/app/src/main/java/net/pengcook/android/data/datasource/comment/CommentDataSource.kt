package net.pengcook.android.data.datasource.comment

import net.pengcook.android.data.model.comment.CommentRequest
import net.pengcook.android.data.model.comment.CommentResponse
import retrofit2.Response

interface CommentDataSource {
    suspend fun fetchComment(
        accessToken: String,
        recipeId: Long,
    ): Response<List<CommentResponse>>

    suspend fun postComment(
        accessToken: String,
        commentRequest: CommentRequest,
    ): Response<Unit>
}
