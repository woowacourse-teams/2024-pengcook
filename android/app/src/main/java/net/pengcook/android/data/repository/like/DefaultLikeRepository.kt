package net.pengcook.android.data.repository.like

import net.pengcook.android.data.datasource.like.LikeRemoteDataSource
import net.pengcook.android.data.util.network.NetworkResponseHandler

class DefaultLikeRepository(
    private val likeRemoteDataSource: LikeRemoteDataSource,
) : LikeRepository,
    NetworkResponseHandler() {
    override suspend fun getLikeCount(recipeId: Long): Result<Int> =
        runCatching {
            val response = likeRemoteDataSource.fetchLikeCount(recipeId)
            body(response, RESPONSE_CODE_SUCCESS)
        }

    override suspend fun postLike(recipeId: Long): Result<Unit> =
        runCatching {
            val response = likeRemoteDataSource.postLike(recipeId)
            body(response, RESPONSE_CODE_SUCCESS)
        }

    companion object {
        private const val RESPONSE_CODE_SUCCESS = 200
    }
}
