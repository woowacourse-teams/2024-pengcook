package net.pengcook.android.data.datasource.feed

import net.pengcook.android.data.model.feed.item.FeedItemResponse2
import net.pengcook.android.data.model.feed.item.FeedItemResponseForList
import net.pengcook.android.data.model.feed.item.RecipeEditRequest
import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.remote.api.FeedService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultFeedRemoteDataSource
    @Inject
    constructor(
        private val feedService: FeedService,
    ) : FeedRemoteDataSource {
        override suspend fun fetchRecipes(
            accessToken: String,
            pageNumber: Int,
            pageSize: Int,
            category: String?,
            keyword: String?,
            userId: Long?,
        ): Response<List<FeedItemResponseForList>> =
            feedService.fetchRecipes(
                accessToken = accessToken,
                accept = "application/vnd.pengcook.v1+json",
                pageNumber = pageNumber,
                pageSize = pageSize,
                category = category,
                keyword = keyword,
                userId = userId,
            )

        override suspend fun fetchRecipeSteps(recipeId: Long): Response<List<RecipeStepResponse>> = feedService.fetchRecipeSteps(recipeId)

        override suspend fun deleteRecipe(
            accessToken: String,
            recipeId: Long,
        ): Response<Unit> = feedService.deleteRecipe(accessToken, recipeId)

        override suspend fun fetchRecipe(
            accessToken: String,
            recipeId: Long,
        ): Response<FeedItemResponse2> = feedService.fetchRecipe(accessToken, recipeId)

        override suspend fun updateRecipe(
            accessToken: String,
            recipeId: Long,
            recipeEditRequest: RecipeEditRequest,
        ): Response<Unit> = feedService.updateRecipe(accessToken, recipeId, recipeEditRequest)
    }
