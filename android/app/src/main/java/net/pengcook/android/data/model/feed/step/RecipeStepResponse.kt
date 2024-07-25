package net.pengcook.android.data.model.feed.step

import com.google.gson.annotations.SerializedName

data class RecipeStepResponse(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("image")
    val image: Any,
    @SerializedName("recipeId")
    val recipeId: Int,
    @SerializedName("sequence")
    val sequence: Int,
)
