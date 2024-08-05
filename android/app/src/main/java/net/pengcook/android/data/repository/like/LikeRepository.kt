package net.pengcook.android.data.repository.like

interface LikeRepository {
    suspend fun getLikeCount(recipeId: Long): Result<Int>

    suspend fun postLike(recipeId: Long): Result<Unit>
}
