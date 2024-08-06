package net.pengcook.android.data.util.mapper

import net.pengcook.android.data.model.makingrecipe.Ingredient
import net.pengcook.android.data.model.makingrecipe.RecipeDescriptionRequest
import net.pengcook.android.domain.model.recipemaking.RecipeDescription

fun RecipeDescription.toRecipeDescriptionRequest(): RecipeDescriptionRequest =
    RecipeDescriptionRequest(
        categories = categories,
        cookingTime = cookingTime,
        description = description,
        difficulty = difficulty,
        ingredients = ingredients.toIngredients(),
        thumbnail = thumbnail,
        title = title,
    )

fun List<String>.toIngredients(): List<Ingredient> = map { name -> Ingredient(name) }
