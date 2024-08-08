package net.pengcook.android.data.datasource.like

import net.pengcook.android.data.model.like.IsLikeRequest
import net.pengcook.android.data.model.like.IsLikeResponse
import retrofit2.Response

interface LikeRemoteDataSource {
    suspend fun fetchLikeCount(accessToken: String, recipeId: Long): Response<IsLikeResponse>

    suspend fun postLike(accessToken: String, isLikeRequest: IsLikeRequest): Response<Unit>
}
