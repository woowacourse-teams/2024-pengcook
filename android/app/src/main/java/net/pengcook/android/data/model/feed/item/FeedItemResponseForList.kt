package net.pengcook.android.data.model.feed.item

data class FeedItemResponseForList(
    val author: AuthorResponse,
    val commentCount: Long,
    val createdAt: String,
    val likeCount: Long,
    val mine: Boolean,
    val recipeId: Long,
    val thumbnail: String,
    val title: String,
)
