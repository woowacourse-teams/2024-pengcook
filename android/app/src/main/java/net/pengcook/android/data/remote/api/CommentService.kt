package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.comment.CommentRequest
import net.pengcook.android.data.model.comment.CommentResponse
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
}
