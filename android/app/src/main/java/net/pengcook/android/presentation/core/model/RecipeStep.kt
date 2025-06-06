package net.pengcook.android.presentation.core.model

data class RecipeStep(
    val stepId: Long,
    val recipeId: Long,
    val description: String,
    val image: String,
    val sequence: Int,
    val cookingTime: String,
)
