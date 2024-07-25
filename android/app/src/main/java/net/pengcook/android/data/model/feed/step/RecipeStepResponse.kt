package net.pengcook.android.data.model.feed.step

data class RecipeStepResponse(
    val description: String,
    val id: Long,
    val image: Any,
    val recipeId: Int,
    val sequence: Int,
)
