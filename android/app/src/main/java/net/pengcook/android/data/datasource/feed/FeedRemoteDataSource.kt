package net.pengcook.android.data.datasource.feed

import net.pengcook.android.data.model.feed.item.FeedItemResponse2
import net.pengcook.android.data.model.feed.item.FeedItemResponseForList
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
    ): Response<List<FeedItemResponseForList>>

    suspend fun fetchRecipeSteps(recipeId: Long): Response<List<RecipeStepResponse>>

    suspend fun deleteRecipe(
        accessToken: String,
        recipeId: Long,
    ): Response<Unit>

    suspend fun fetchRecipe(
        accessToken: String,
        recipeId: Long,
    ): Response<FeedItemResponse2>
}
