package net.pengcook.android.data.model.makingrecipe.request

data class RecipeCreationRequest(
    val categories: List<String>,
    val cookingTime: String,
    val description: String,
    val difficulty: Int,
    val ingredients: List<IngredientRequest>,
    val recipeSteps: List<RecipeStepRequest>,
    val thumbnail: String,
    val title: String,
)
