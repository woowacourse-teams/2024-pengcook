package net.pengcook.android.data.datasource.feed

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.remote.api.FeedService
import retrofit2.Response

class DefaultFeedRemoteDataSource(
    private val feedService: FeedService,
) : FeedRemoteDataSource {
    override suspend fun fetchRecipes(
        accessToken: String,
        pageNumber: Int,
        pageSize: Int,
        category: String?,
        keyword: String?,
        userId: Long?,
    ): Response<List<FeedItemResponse>> = feedService.fetchRecipes(accessToken, pageNumber, pageSize, category, keyword, userId)

    override suspend fun fetchRecipeSteps(recipeId: Long): Response<List<RecipeStepResponse>> = feedService.fetchRecipeSteps(recipeId)
}
