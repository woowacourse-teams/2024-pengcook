package net.pengcook.android.domain.model.recipemaking

data class RecipeDescription(
    val recipeDescriptionId: Long,
    val categories: List<String>,
    val cookingTime: String,
    val description: String,
    val difficulty: Int,
    val ingredients: List<String>,
    val thumbnail: String,
    val title: String,
    val imageUri: String,
)
