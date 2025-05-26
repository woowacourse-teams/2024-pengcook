package net.pengcook.android.data.model.step

data class RecipeStepResponse(
    val id: Long,
    val description: String,
    val image: String,
    val recipeId: Long,
    val sequence: Int,
    val cookingTime: String,
)
