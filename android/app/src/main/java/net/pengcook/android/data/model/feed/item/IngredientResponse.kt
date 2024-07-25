package net.pengcook.android.data.model.feed.item

import com.google.gson.annotations.SerializedName

data class IngredientResponse(
    @SerializedName("ingredientId")
    val ingredientId: Long,
    @SerializedName("ingredientName")
    val ingredientName: String,
    @SerializedName("requirement")
    val requirement: String,
)
