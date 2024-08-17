package net.pengcook.android.data.util.mapper

import net.pengcook.android.data.model.makingrecipe.entity.CategoryEntity
import net.pengcook.android.data.model.makingrecipe.entity.IngredientEntity
import net.pengcook.android.data.model.makingrecipe.entity.RecipeDescriptionEntity
import net.pengcook.android.domain.model.recipemaking.RecipeDescription

fun RecipeDescription.toRecipeDescriptionEntity(recipeId: Long): RecipeDescriptionEntity =
    RecipeDescriptionEntity(
        id = recipeId,
        cookingTime = cookingTime,
        description = description,
        difficulty = difficulty,
        thumbnail = thumbnail,
        title = title,
        imageUri = imageUri,
    )

fun List<String>.toIngredientEntities(recipeId: Long): List<IngredientEntity> =
    map { name ->
        IngredientEntity(
            recipeId = recipeId,
            name = name,
        )
    }

fun List<String>.toCategoryEntities(recipeId: Long): List<CategoryEntity> =
    map { name ->
        CategoryEntity(
            recipeId = recipeId,
            categoryName = name,
        )
    }
