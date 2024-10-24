package net.pengcook.android.data.model.feed.item

import net.pengcook.android.data.model.makingrecipe.request.IngredientRequest
import net.pengcook.android.data.model.makingrecipe.request.RecipeStepRequest

data class RecipeEditRequest(
    val title: String,
    val cookingTime: String,
    val thumbnail: String,
    val difficulty: Int,
    val description: String,
    val categories: List<String>,
    val ingredients: List<IngredientRequest>,
    val recipeSteps: List<RecipeStepRequest>,
)
