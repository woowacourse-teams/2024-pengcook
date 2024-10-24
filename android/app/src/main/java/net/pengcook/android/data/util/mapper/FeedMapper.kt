package net.pengcook.android.data.util.mapper

import net.pengcook.android.data.model.feed.item.AuthorResponse
import net.pengcook.android.data.model.feed.item.CategoryResponse
import net.pengcook.android.data.model.feed.item.FeedItemResponse2
import net.pengcook.android.data.model.feed.item.FeedItemResponseForList
import net.pengcook.android.data.model.feed.item.IngredientResponse
import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.presentation.core.model.Ingredient
import net.pengcook.android.presentation.core.model.RecipeForItem
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.core.model.RecipeStep
import net.pengcook.android.presentation.core.model.User

fun FeedItemResponseForList.toRecipeForList(): RecipeForList =
    RecipeForList(
        title = title,
        recipeId = recipeId,
        thumbnail = thumbnail,
        user = author.toUser(),
        likeCount = likeCount,
        commentCount = commentCount,
        mine = mine,
        createdAt = createdAt,
    )

fun FeedItemResponse2.toRecipeForItem(): RecipeForItem =
    RecipeForItem(
        title = title,
        recipeId = recipeId,
        thumbnail = thumbnail,
        user = author.toUser(),
        likeCount = likeCount,
        commentCount = commentCount,
        mine = mine,
        cookingTime = cookingTime,
        difficulty = difficulty,
        category = category.map(CategoryResponse::toCategoryText),
        ingredients = ingredient.map(IngredientResponse::toIngredient),
        introduction = description,
        isLiked = isLike,
    )

// fun FeedItemResponse.toRecipe(): Recipe =
//    Recipe(
//        title = title,
//        recipeId = recipeId,
//        category = category.map(CategoryResponse::toCategoryText),
//        cookingTime = cookingTime,
//        thumbnail = thumbnail,
//        user = author.toUser(),
//        likeCount = likeCount,
//        ingredients = ingredient.map(IngredientResponse::toIngredient),
//        difficulty = difficulty,
//        introduction = description,
//        commentCount = commentCount,
//        mine = mine,
//    )

fun RecipeStepResponse.toRecipeStep(): RecipeStep =
    RecipeStep(
        stepId = stepId,
        recipeId = recipeId,
        description = description,
        image = image,
        sequence = sequence,
        cookingTime = cookingTime,
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
