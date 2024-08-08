package net.pengcook.android.data.repository.comment

import net.pengcook.android.data.datasource.comment.CommentDataSource
import net.pengcook.android.data.model.comment.CommentRequest
import net.pengcook.android.data.model.comment.CommentResponse
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.Comment

class FakeCommentRepository(
    private val dataSource: CommentDataSource,
) : NetworkResponseHandler(),
    CommentRepository {
    override suspend fun fetchComments(recipeId: Long): Result<List<Comment>> =
        runCatching {
            val response = dataSource.fetchComment("accessToken", recipeId)
            body(response, RESPONSE_CODE_SUCCESS).map { it.toComment() }
        }

    override suspend fun postComment(
        recipeId: Long,
        message: String,
    ): Result<Unit> =
        runCatching {
            val response =
                dataSource.postComment(
                    accessToken = "accessToken",
                    commentRequest =
                        CommentRequest(
                            recipeId = recipeId,
                            message = message,
                        ),
                )
            body(response, RESPONSE_CODE_SUCCESS)
        }

    private fun CommentResponse.toComment(): Comment =
        Comment(
            commentId = commentId,
            userId = userId,
            userImage = userImage,
            userName = userName,
            createdAt = createdAt,
            message = message,
            mine = mine,
        )

    companion object {
        private const val EXCEPTION_HTTP_CODE = "Http code is not appropriate."
        private const val EXCEPTION_NULL_BODY = "Response body is null."
        private const val RESPONSE_CODE_SUCCESS = 200
    }
}
