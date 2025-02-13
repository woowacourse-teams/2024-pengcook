package net.pengcook.android.data.repository.comment

import kotlinx.coroutines.flow.first
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.comment.CommentDataSource
import net.pengcook.android.data.model.comment.CommentRequest
import net.pengcook.android.data.model.comment.CommentResponse
import net.pengcook.android.data.model.comment.MyCommentResponse
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.Comment
import net.pengcook.android.presentation.core.model.MyComment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCommentRepository
    @Inject
    constructor(
        private val sessionLocalDataSource: SessionLocalDataSource,
        private val commentDataSource: CommentDataSource,
    ) : NetworkResponseHandler(),
        CommentRepository {
        override suspend fun fetchComments(recipeId: Long): Result<List<Comment>> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = commentDataSource.fetchComment(accessToken, recipeId)
                body(response, RESPONSE_CODE_SUCCESS).map { it.toComment() }
            }

        override suspend fun fetchMyComments(): Result<List<MyComment>> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = commentDataSource.fetchMyComments(accessToken)
                body(response, RESPONSE_CODE_SUCCESS).map { it.toMyComment() }
            }

        override suspend fun postComment(
            recipeId: Long,
            message: String,
        ): Result<Unit> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val commentRequest =
                    CommentRequest(
                        recipeId = recipeId,
                        message = message,
                    )
                val response = commentDataSource.postComment(accessToken, commentRequest)
                body(response, VALID_POST_CODE)
            }

        override suspend fun deleteComment(commentId: Long): Result<Unit> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = commentDataSource.deleteComment(accessToken, commentId)
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

        private fun MyCommentResponse.toMyComment(): MyComment =
            MyComment(
                commentId = commentId,
                recipeId = recipeId,
                recipeTitle = recipeTitle,
                createdAt = createdAt,
                message = message,
                recipeImage = recipeThumbnail,
            )

        companion object {
            private const val VALID_POST_CODE = 201
            private const val RESPONSE_CODE_SUCCESS = 200
        }
    }
