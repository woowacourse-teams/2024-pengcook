package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.feed.step.RecipeStepResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {
    @GET("/api/recipes")
    suspend fun fetchRecipes(
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<List<FeedItemResponse>>

    @GET("/api/recipes/{recipeId}/steps")
    suspend fun fetchRecipeSteps(
        @Path("recipeId") recipeId: Long,
    ): Response<List<RecipeStepResponse>>

    @GET("/api/categories")
    suspend fun fetchRecipesByCategory(
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("category") category: String,
    ): Response<List<FeedItemResponse>>
}