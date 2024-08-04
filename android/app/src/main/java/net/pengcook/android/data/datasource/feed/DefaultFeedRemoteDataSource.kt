package net.pengcook.android.data.datasource.feed

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.remote.api.FeedService
import retrofit2.Response

class DefaultFeedRemoteDataSource(
    private val feedService: FeedService,
) : FeedRemoteDataSource {
    override suspend fun fetchRecipes(
        pageNumber: Int,
        pageSize: Int,
    ): Response<List<FeedItemResponse>> = feedService.fetchRecipes(pageNumber, pageSize)

    override suspend fun fetchRecipeSteps(recipeId: Long): Response<List<RecipeStepResponse>> = feedService.fetchRecipeSteps(recipeId)

    override suspend fun fetchRecipesByCategory(
        pageNumber: Int,
        pageSize: Int,
        category: String,
    ): Response<List<FeedItemResponse>> = feedService.fetchRecipesByCategory(pageNumber, pageSize, category)
}
