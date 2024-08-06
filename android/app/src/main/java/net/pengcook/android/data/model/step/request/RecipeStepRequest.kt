package net.pengcook.android.data.model.step.request

data class RecipeStepRequest(
    val image: String,
    val description: String,
    val sequence: Int,
    val cookingTime: String,
)
