package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.comment.CommentRequest
import net.pengcook.android.data.model.comment.CommentResponse
import net.pengcook.android.data.model.comment.MyCommentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentService {
    @GET("/comments/{recipeId}")
    suspend fun fetchComments(
        @Header("Authorization") accessToken: String,
        @Path("recipeId") recipeId: Long,
    ): Response<List<CommentResponse>>

    @GET("/comments/mine")
    suspend fun fetchMyComments(
        @Header("Authorization") accessToken: String,
        @Header("Accept") accept: String = MY_COMMENT_ACCEPT,
    ): Response<List<MyCommentResponse>>

    @POST("/comments")
    suspend fun postComment(
        @Header("Authorization") accessToken: String,
        @Body commentRequest: CommentRequest,
    ): Response<Unit>

    @DELETE("/comments/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization") accessToken: String,
        @Path("commentId") commentId: Long,
    ): Response<Unit>

    companion object {
        const val MY_COMMENT_ACCEPT = "application/vnd.pengcook.v1+json"
    }
}
