package net.pengcook.android.data.datasource.comment

import net.pengcook.android.data.model.comment.CommentRequest
import net.pengcook.android.data.model.comment.CommentResponse
import net.pengcook.android.data.model.comment.MyCommentResponse
import retrofit2.Response

class FakeCommentDataSource : CommentDataSource {
    private val comments =
        mutableListOf(
            CommentRecord(
                recipeId = 1L,
                commentId = 1L,
                userId = 1L,
                userImage = "https://upload.wikimedia.org/wikipedia/commons/0/07/Emperor_Penguin_Manchot_empereur.jpg",
                userName = "Peng Cook",
                createdAt = "2021-09-01T00:00:00Z",
                message = "Akeo is the best!",
                mine = false,
            ),
            CommentRecord(
                recipeId = 1L,
                commentId = 2L,
                userId = 2L,
                userImage = "https://cdn6.aptoide.com/imgs/d/d/b/ddb43d179af9d23ef5c0e47a137fbf01_icon.png",
                userName = "ATO",
                createdAt = "2021-09-01T00:00:00Z",
                message = "Akeo is the best!",
                mine = false,
            ),
        )

    override suspend fun fetchComment(
        accessToken: String,
        recipeId: Long,
    ): Response<List<CommentResponse>> {
        val result = comments.filter { it.recipeId == recipeId }.map { it.toCommentResponse() }
        return Response.success(result)
    }

    override suspend fun fetchMyComments(accessToken: String): Response<List<MyCommentResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun postComment(
        accessToken: String,
        commentRequest: CommentRequest,
    ): Response<Unit> {
        comments.add(commentRequest.toCommentRecord())
        return Response.success(Unit)
    }

    override suspend fun deleteComment(
        accessToken: String,
        commentId: Long,
    ): Response<Unit> {
        comments.removeIf { it.commentId == commentId }
        return Response.success(Unit)
    }

    private fun CommentRecord.toCommentResponse() =
        CommentResponse(
            commentId = commentId,
            userId = userId,
            userImage = userImage,
            userName = userName,
            createdAt = createdAt,
            message = message,
            mine = mine,
        )

    private fun CommentRequest.toCommentRecord() =
        CommentRecord(
            recipeId = recipeId,
            commentId = comments.size + 1L,
            userId = 3L,
            userImage = "https://i.namu.wiki/i/E3cTiowmvsUAQ4YDAFspMd2vwe5rY4F_qzK9ocuISac1cfQ1DMmMz2NiEldWfK_GTG1jiwwqSPn_FxTv5xRyvA.webp",
            userName = "Loki",
            createdAt = "2021-09-01T00:00:00Z",
            message = message,
            mine = false,
        )
}

data class CommentRecord(
    val recipeId: Long,
    val commentId: Long,
    val userId: Long,
    val userImage: String,
    val userName: String,
    val createdAt: String,
    val message: String,
    val mine: Boolean,
)
