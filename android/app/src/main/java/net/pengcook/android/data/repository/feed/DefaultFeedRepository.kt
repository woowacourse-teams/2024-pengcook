package net.pengcook.android.data.repository.feed

import kotlinx.coroutines.flow.first
import net.pengcook.android.data.datasource.feed.FeedRemoteDataSource
import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.data.util.mapper.toRecipe
import net.pengcook.android.data.util.mapper.toRecipeStep
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.RecipeStep

class DefaultFeedRepository(
    private val sessionRepository: SessionRepository,
    private val feedRemoteDataSource: FeedRemoteDataSource,
) : FeedRepository,
    NetworkResponseHandler() {
    override suspend fun fetchRecipes(
        pageNumber: Int,
        pageSize: Int,
        category: String?,
        keyword: String?,
        userId: Long?,
    ): Result<List<Recipe>> =
        runCatching {
            val accessToken =
                sessionRepository.sessionData.first().accessToken ?: throw RuntimeException()
            val response =
                feedRemoteDataSource.fetchRecipes(accessToken, pageNumber, pageSize, category, keyword, userId)
            body(response, RESPONSE_CODE_SUCCESS).map(FeedItemResponse::toRecipe)
        }

    override suspend fun fetchRecipeSteps(recipeId: Long): Result<List<RecipeStep>> =
        runCatching {
            val response = feedRemoteDataSource.fetchRecipeSteps(recipeId)
            body(response, RESPONSE_CODE_SUCCESS).map(RecipeStepResponse::toRecipeStep)
        }

    companion object {
        private const val RESPONSE_CODE_SUCCESS = 200
    }
}
