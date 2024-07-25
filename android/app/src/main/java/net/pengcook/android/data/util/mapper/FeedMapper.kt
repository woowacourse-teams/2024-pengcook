package net.pengcook.android.data.util.mapper

import net.pengcook.android.data.model.feed.item.AuthorResponse
import net.pengcook.android.data.model.feed.item.CategoryResponse
import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.feed.item.IngredientResponse
import net.pengcook.android.data.model.feed.step.RecipeStepResponse
import net.pengcook.android.presentation.core.model.Ingredient
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.User
import net.pengcook.android.presentation.detail.RecipeStep

fun FeedItemResponse.toRecipe(): Recipe =
    Recipe(
        title = title,
        recipeId = recipeId,
        category = category.map(CategoryResponse::toCategoryText),
        cookingTime = cookingTime,
        thumbnail = thumbnail,
        user = author.toUser(),
        favoriteCount = likeCount,
        ingredients = ingredient.map(IngredientResponse::toIngredient),
        difficulty = difficulty,
        introduction = description,
        commentCount = 0,
    )

fun RecipeStepResponse.toRecipeStep(): RecipeStep =
    RecipeStep(
        stepId = id,
        recipeId = recipeId,
        description = description,
        image = image,
        sequence = sequence,
    )

private fun CategoryResponse.toCategoryText(): String = categoryName

private fun AuthorResponse.toUser(): User =
    User(
        id = authorId,
        username = authorName,
        profile = authorImage,
    )

private fun IngredientResponse.toIngredient(): Ingredient =
    Ingredient(
        ingredientId = ingredientId,
        ingredientName = ingredientName,
        requirement = requirement,
    )
