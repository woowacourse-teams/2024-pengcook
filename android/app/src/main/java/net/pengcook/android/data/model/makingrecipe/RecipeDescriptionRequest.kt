package net.pengcook.android.data.model.makingrecipe

data class RecipeDescriptionRequest(
    val categories: List<String>,
    val cookingTime: String = "00:00:00",
    val description: String,
    val difficulty: Int,
    val ingredients: List<Ingredient>,
    val thumbnail: String,
    val title: String,
)
