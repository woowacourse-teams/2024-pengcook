package net.pengcook.android.data.datasource.feed

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.step.RecipeStepResponse
import retrofit2.Response

interface FeedRemoteDataSource {
    suspend fun fetchRecipes(
        accessToken: String,
        pageNumber: Int,
        pageSize: Int,
        category: String?,
        keyword: String?,
        userId: Long?,
    ): Response<List<FeedItemResponse>>

    suspend fun fetchRecipeSteps(recipeId: Long): Response<List<RecipeStepResponse>>

    suspend fun deleteRecipe(
        accessToken: String,
        recipeId: Long,
    ): Response<Unit>
}
