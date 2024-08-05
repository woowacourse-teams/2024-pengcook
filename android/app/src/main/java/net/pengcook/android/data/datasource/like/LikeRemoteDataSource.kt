package net.pengcook.android.data.datasource.like

import retrofit2.Response

interface LikeRemoteDataSource {
    suspend fun fetchLikeCount(recipeId: Long): Response<Int>

    suspend fun postLike(recipeId: Long): Response<Unit>
}
