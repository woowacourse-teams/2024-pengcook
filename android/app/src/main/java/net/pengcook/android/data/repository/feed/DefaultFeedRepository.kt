package net.pengcook.android.data.repository.feed

import kotlinx.coroutines.flow.first
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.feed.FeedRemoteDataSource
import net.pengcook.android.data.model.feed.item.FeedItemResponseForList
import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.util.mapper.toRecipeEditRequest
import net.pengcook.android.data.util.mapper.toRecipeForItem
import net.pengcook.android.data.util.mapper.toRecipeForList
import net.pengcook.android.data.util.mapper.toRecipeStep
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.ChangedRecipe
import net.pengcook.android.presentation.core.model.RecipeForItem
import net.pengcook.android.presentation.core.model.RecipeForList
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
        ): Result<List<RecipeForList>> =
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
                body(response, RESPONSE_CODE_SUCCESS).map(FeedItemResponseForList::toRecipeForList)
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

        override suspend fun fetchRecipe(recipeId: Long): Result<RecipeForItem> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = feedRemoteDataSource.fetchRecipe(accessToken, recipeId)
                body(response, RESPONSE_CODE_SUCCESS).toRecipeForItem()
            }

        override suspend fun updateRecipe(
            recipeId: Long,
            changedRecipe: ChangedRecipe,
        ): Result<Unit> =
            kotlin.runCatching {
                val accessToken = sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = feedRemoteDataSource.updateRecipe(accessToken, recipeId, changedRecipe.toRecipeEditRequest())
                body(response, RESPONSE_CODE_SUCCESS)
            }

        companion object {
            private const val RESPONSE_CODE_SUCCESS = 200
            private const val RESPONSE_CODE_DELETED = 204
        }
    }
