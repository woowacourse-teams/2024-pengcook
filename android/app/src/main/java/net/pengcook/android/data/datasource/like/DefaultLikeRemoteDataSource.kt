package net.pengcook.android.data.datasource.like

import net.pengcook.android.data.model.like.IsLikeRequest
import net.pengcook.android.data.model.like.IsLikeResponse
import net.pengcook.android.data.remote.api.LikeService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultLikeRemoteDataSource @Inject constructor(
    private val likeService: LikeService,
) : LikeRemoteDataSource {
    override suspend fun fetchIsLike(
        accessToken: String,
        recipeId: Long,
    ): Response<IsLikeResponse> = likeService.fetchLikeCount(accessToken, recipeId)

    override suspend fun postLike(
        accessToken: String,
        isLikeRequest: IsLikeRequest,
    ): Response<Unit> = likeService.postLike(accessToken, isLikeRequest)
}
