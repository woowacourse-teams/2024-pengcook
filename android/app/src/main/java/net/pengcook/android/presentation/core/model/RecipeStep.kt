package net.pengcook.android.presentation.core.model

data class RecipeStep(
    val stepId: Long,
    val recipeId: Long,
    val description: String,
    val image: Any,
    val sequence: Long,
)
