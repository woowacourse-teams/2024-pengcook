package net.pengcook.android.data.repository.like

import kotlinx.coroutines.flow.first
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.like.LikeRemoteDataSource
import net.pengcook.android.data.model.like.IsLikeRequest
import net.pengcook.android.data.util.network.NetworkResponseHandler

class DefaultLikeRepository(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val likeRemoteDataSource: LikeRemoteDataSource,
) : LikeRepository,
    NetworkResponseHandler() {
    override suspend fun getIsLike(recipeId: Long): Result<Boolean> =
        runCatching {
            val accessToken =
                sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
            val response =
                likeRemoteDataSource.fetchLikeCount(accessToken = accessToken, recipeId = recipeId)
            body(response, RESPONSE_CODE_SUCCESS).isLike
        }

    override suspend fun postLike(
        recipeId: Long,
        isLike: Boolean,
    ): Result<Unit> =
        runCatching {
            val accessToken =
                sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
            val response =
                likeRemoteDataSource.postLike(
                    accessToken = accessToken,
                    IsLikeRequest(recipeId, isLike),
                )
            body(response, RESPONSE_CODE_SUCCESS)
        }

    companion object {
        private const val RESPONSE_CODE_SUCCESS = 200
    }
}