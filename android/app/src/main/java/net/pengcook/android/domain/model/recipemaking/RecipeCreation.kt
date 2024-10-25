package net.pengcook.android.domain.model.recipemaking

import net.pengcook.android.presentation.core.model.Ingredient
import net.pengcook.android.presentation.core.model.RecipeStepMaking

data class RecipeCreation(
    val recipeId: Long = 1L,
    val title: String,
    val introduction: String,
    val cookingTime: String,
    val difficulty: Int,
    val ingredients: List<Ingredient>,
    val categories: List<String>,
    val thumbnail: String,
    val steps: List<RecipeStepMaking>,
)
