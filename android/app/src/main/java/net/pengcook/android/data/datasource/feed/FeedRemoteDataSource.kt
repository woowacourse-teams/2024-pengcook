package net.pengcook.android.data.datasource.feed

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.feed.step.RecipeStepResponse
import retrofit2.Response

interface FeedRemoteDataSource {
    suspend fun fetchRecipes(
        pageNumber: Int,
        pageSize: Int,
    ): Response<List<FeedItemResponse>>

    suspend fun fetchRecipeSteps(
        recipeId: Long
    ): Response<List<RecipeStepResponse>>

    suspend fun fetchRecipesByCategory(
        pageNumber: Int,
        pageSize: Int,
        category: String,
    ): Response<List<FeedItemResponse>>
}
