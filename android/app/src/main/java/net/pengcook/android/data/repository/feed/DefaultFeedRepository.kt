package net.pengcook.android.data.repository.feed

import kotlinx.coroutines.flow.first
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.feed.FeedRemoteDataSource
import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.util.mapper.toRecipe
import net.pengcook.android.data.util.mapper.toRecipeStep
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.RecipeStep
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultFeedRepository
    @Inject
    constructor(
        private val sessionLocalDataSource: SessionLocalDataSource,
        private val feedRemoteDataSource: FeedRemoteDataSource,
    ) : NetworkResponseHandler(),
        FeedRepository {
        override suspend fun fetchRecipes(
            pageNumber: Int,
            pageSize: Int,
            category: String?,
            keyword: String?,
            userId: Long?,
        ): Result<List<Recipe>> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response =
                    feedRemoteDataSource.fetchRecipes(
                        accessToken,
                        pageNumber,
                        pageSize,
                        category,
                        keyword,
                        userId,
                    )
                body(response, RESPONSE_CODE_SUCCESS).map(FeedItemResponse::toRecipe)
            }

        override suspend fun fetchRecipeSteps(recipeId: Long): Result<List<RecipeStep>> =
            runCatching {
                val response = feedRemoteDataSource.fetchRecipeSteps(recipeId)
                body(response, RESPONSE_CODE_SUCCESS).map(RecipeStepResponse::toRecipeStep)
            }

        override suspend fun deleteRecipe(recipeId: Long): Result<Unit> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = feedRemoteDataSource.deleteRecipe(accessToken, recipeId)
                body(response, RESPONSE_CODE_DELETED)
            }

        companion object {
            private const val RESPONSE_CODE_SUCCESS = 200
            private const val RESPONSE_CODE_DELETED = 204
        }
    }
