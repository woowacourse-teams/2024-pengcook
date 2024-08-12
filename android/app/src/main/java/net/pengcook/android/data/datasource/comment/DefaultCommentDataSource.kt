package net.pengcook.android.data.datasource.comment

import net.pengcook.android.data.model.comment.CommentRequest
import net.pengcook.android.data.model.comment.CommentResponse
import net.pengcook.android.data.remote.api.CommentService
import retrofit2.Response

class DefaultCommentDataSource(
    private val commentService: CommentService,
) : CommentDataSource {
    override suspend fun fetchComment(
        accessToken: String,
        recipeId: Long,
    ): Response<List<CommentResponse>> = commentService.fetchComments(accessToken, recipeId)

    override suspend fun postComment(
        accessToken: String,
        commentRequest: CommentRequest,
    ): Response<Unit> = commentService.postComment(accessToken, commentRequest)

    override suspend fun deleteComment(
        accessToken: String,
        commentId: Long,
    ): Response<Unit> = commentService.deleteComment(accessToken, commentId)
}
