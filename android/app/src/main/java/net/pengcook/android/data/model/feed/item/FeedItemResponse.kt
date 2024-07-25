package net.pengcook.android.data.model.feed.item

data class FeedItemResponse(
    val author: AuthorResponse,
    val category: List<CategoryResponse>,
    val cookingTime: String,
    val description: String,
    val difficulty: Int,
    val ingredient: List<IngredientResponse>,
    val likeCount: Long,
    val recipeId: Long,
    val thumbnail: String,
    val title: String,
)
