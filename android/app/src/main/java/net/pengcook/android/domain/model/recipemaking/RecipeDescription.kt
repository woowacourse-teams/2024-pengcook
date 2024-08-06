package net.pengcook.android.domain.model.recipemaking

data class RecipeDescription(
    val categories: List<String>,
    val cookingTime: String,
    val description: String,
    val difficulty: Int,
    val ingredients: List<String>,
    val thumbnail: String,
    val title: String,
)
