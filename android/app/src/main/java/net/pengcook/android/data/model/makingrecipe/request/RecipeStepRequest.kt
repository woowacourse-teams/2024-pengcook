package net.pengcook.android.data.model.makingrecipe.request

data class RecipeStepRequest(
    val cookingTime: String,
    val description: String,
    val image: String,
    val sequence: Int,
)
