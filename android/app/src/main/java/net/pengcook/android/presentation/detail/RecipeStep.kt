package net.pengcook.android.presentation.detail

data class RecipeStep(
    val stepId: Long,
    val recipeId: Int,
    val description: String,
    val image: Any,
    val sequence: Int
)
