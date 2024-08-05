package net.pengcook.android.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LikeService {
    @GET("/api/likes/{recipeId}")
    suspend fun fetchLikeCount(
        @Path("recipeId") recipeId: Long,
    ): Response<Int>

    @POST("/api/likes/{recipeId}")
    suspend fun postLike(
        @Path("recipeId") recipeId: Long,
    ): Response<Unit>
}
