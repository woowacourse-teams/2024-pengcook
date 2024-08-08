package net.pengcook.android.data.model.like

data class IsLikeRequest(
    val recipeId: Long,
    val isLike: Boolean,
)
