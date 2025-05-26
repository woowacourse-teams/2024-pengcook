package net.pengcook.android.presentation.core.model

data class ChangedRecipe(
    val title: String,
    val cookingTime: String,
    val thumbnail: String,
    val difficulty: Int,
    val description: String,
    val categories: List<String>,
    val ingredients: List<Ingredient>,
    val recipeSteps: List<RecipeStep>,
)
