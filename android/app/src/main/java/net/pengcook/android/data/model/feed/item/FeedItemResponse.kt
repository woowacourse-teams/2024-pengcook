package net.pengcook.android.data.model.feed.item

import com.google.gson.annotations.SerializedName

data class FeedItemResponse(
    @SerializedName("author")
    val author: AuthorResponse,
    @SerializedName("category")
    val category: List<CategoryResponse>,
    @SerializedName("cookingTime")
    val cookingTime: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("difficulty")
    val difficulty: Int,
    @SerializedName("ingredient")
    val ingredient: List<IngredientResponse>,
    @SerializedName("likeCount")
    val likeCount: Int,
    @SerializedName("recipeId")
    val recipeId: Long,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("title")
    val title: String,
)
