package net.pengcook.android.data.repository.like

interface LikeRepository {
    suspend fun getIsLike(recipeId: Long): Result<Boolean>

    suspend fun postLike(
        recipeId: Long,
        isLike: Boolean,
    ): Result<Unit>
}
