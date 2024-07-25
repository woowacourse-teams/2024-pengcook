package net.pengcook.android.data.repository.feed

import net.pengcook.android.data.datasource.feed.FeedRemoteDataSource
import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.feed.step.RecipeStepResponse
import net.pengcook.android.data.util.mapper.toFeed
import net.pengcook.android.data.util.mapper.toRecipeStep
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.Feed
import net.pengcook.android.presentation.detail.RecipeStep
import retrofit2.Response

class DefaultFeedRepository(
    private val feedRemoteDataSource: FeedRemoteDataSource,
) : FeedRepository, NetworkResponseHandler {
    override suspend fun fetchRecipes(
        pageNumber: Int,
        pageSize: Int,
    ): Result<List<Feed>> {
        return runCatching {
            val response = feedRemoteDataSource.fetchRecipes(pageNumber, pageSize)
            body(response, RESPONSE_CODE_SUCCESS).map(FeedItemResponse::toFeed)
        }
    }

    override suspend fun fetchRecipeSteps(recipeId: Long): Result<List<RecipeStep>> {
        return runCatching {
            val response = feedRemoteDataSource.fetchRecipeSteps(recipeId)
            body(response, RESPONSE_CODE_SUCCESS).map(RecipeStepResponse::toRecipeStep)
        }
    }

    override suspend fun fetchRecipesByCategory(
        pageNumber: Int,
        pageSize: Int,
        category: String,
    ): Result<List<Feed>> {
        return runCatching {
            val response =
                feedRemoteDataSource.fetchRecipesByCategory(pageNumber, pageSize, category)
            body(response, RESPONSE_CODE_SUCCESS).map(FeedItemResponse::toFeed)
        }
    }

    override fun <T> body(
        response: Response<T>,
        validHttpCode: Int,
    ): T {
        val code = response.code()
        val body = response.body()
        if (code != validHttpCode) throw RuntimeException(EXCEPTION_HTTP_CODE)
        if (body == null) throw RuntimeException(EXCEPTION_NULL_BODY)
        return body
    }

    companion object {
        private const val EXCEPTION_HTTP_CODE = "Http code is not appropriate."
        private const val EXCEPTION_NULL_BODY = "Response body is null."
        private const val RESPONSE_CODE_SUCCESS = 200
    }
}
