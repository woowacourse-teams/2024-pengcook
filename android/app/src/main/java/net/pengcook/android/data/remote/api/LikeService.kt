package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.like.IsLikeRequest
import net.pengcook.android.data.model.like.IsLikeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface LikeService {
    @GET("likes/{recipeId}")
    suspend fun fetchLikeCount(
        @Header("Authorization") accessToken: String,
        @Path("recipeId") recipeId: Long,
    ): Response<IsLikeResponse>

    @POST("likes")
    suspend fun postLike(
        @Header("Authorization") accessToken: String,
        @Body isLikeRequest: IsLikeRequest,
    ): Response<Unit>
}
