package net.pengcook.android.data.datasource.like

import net.pengcook.android.data.remote.api.LikeService
import retrofit2.Response

class DefaultLikeRemoteDataSource(
    private val likeService: LikeService,
) : LikeRemoteDataSource {
    override suspend fun fetchLikeCount(recipeId: Long): Response<Int> = likeService.fetchLikeCount(recipeId)

    override suspend fun postLike(recipeId: Long): Response<Unit> = likeService.postLike(recipeId)
}
