package net.pengcook.android.data.repository.feed

import net.pengcook.android.data.datasource.feed.FeedRemoteDataSource
import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.feed.step.RecipeStepResponse
import net.pengcook.android.data.util.mapper.toRecipe
import net.pengcook.android.data.util.mapper.toRecipeStep
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.RecipeStep
import retrofit2.Response

class DefaultFeedRepository(
    private val feedRemoteDataSource: FeedRemoteDataSource,
) : FeedRepository,
    NetworkResponseHandler() {
    override suspend fun fetchRecipes(
        pageNumber: Int,
        pageSize: Int,
    ): Result<List<Recipe>> =
        runCatching {
            val response = feedRemoteDataSource.fetchRecipes(pageNumber, pageSize)
            body(response, RESPONSE_CODE_SUCCESS).map(FeedItemResponse::toRecipe)
        }

    override suspend fun fetchRecipeSteps(recipeId: Long): Result<List<RecipeStep>> =
        runCatching {
            val response = feedRemoteDataSource.fetchRecipeSteps(recipeId)
            body(response, RESPONSE_CODE_SUCCESS).map(RecipeStepResponse::toRecipeStep)
        }

    override suspend fun fetchRecipesByCategory(
        pageNumber: Int,
        pageSize: Int,
        category: String,
    ): Result<List<Recipe>> =
        runCatching {
            val response =
                feedRemoteDataSource.fetchRecipesByCategory(pageNumber, pageSize, category)
            body(response, RESPONSE_CODE_SUCCESS).map(FeedItemResponse::toRecipe)
        }

    companion object {
        private const val RESPONSE_CODE_SUCCESS = 200
    }
}
