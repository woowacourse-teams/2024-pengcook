package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.feed.item.FeedItemResponse2
import net.pengcook.android.data.model.feed.item.FeedItemResponseForList
import net.pengcook.android.data.model.step.RecipeStepResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {
    @GET("/recipes")
    suspend fun fetchRecipes(
        @Header("Authorization") accessToken: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("category") category: String?,
        @Query("keyword") keyword: String?,
        @Query("userId") userId: Long?,
    ): Response<List<FeedItemResponse>>

    @GET("/recipes")
    suspend fun fetchRecipes2(
        @Header("Authorization") accessToken: String,
        @Header("Accept") accept: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("category") category: String?,
        @Query("keyword") keyword: String?,
        @Query("userId") userId: Long?,
    ): Response<List<FeedItemResponseForList>>

    @GET("/recipes/{recipeId}/steps")
    suspend fun fetchRecipeSteps(
        @Path("recipeId") recipeId: Long,
    ): Response<List<RecipeStepResponse>>

    @DELETE("/recipes/{recipeId}")
    suspend fun deleteRecipe(
        @Header("Authorization") accessToken: String,
        @Path("recipeId") recipeId: Long,
    ): Response<Unit>

    @GET("/recipes/{recipeId}")
    suspend fun fetchRecipe(
        @Header("Authorization") accessToken: String,
        @Path("recipeId") recipeId: Long,
    ): Response<FeedItemResponse2>
}
