package net.pengcook.android.data.util.mapper

import net.pengcook.android.data.model.makingrecipe.entity.CategoryEntity
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipe
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipeDescription
import net.pengcook.android.data.model.makingrecipe.entity.IngredientEntity
import net.pengcook.android.data.model.makingrecipe.entity.RecipeDescriptionEntity
import net.pengcook.android.data.model.makingrecipe.request.IngredientRequest
import net.pengcook.android.data.model.makingrecipe.request.RecipeCreationRequest
import net.pengcook.android.data.model.makingrecipe.request.RecipeStepRequest
import net.pengcook.android.data.model.step.RecipeStepEntity
import net.pengcook.android.domain.model.recipemaking.RecipeCreation
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import net.pengcook.android.presentation.core.model.Ingredient
import net.pengcook.android.presentation.core.model.RecipeStepMaking

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

fun CreatedRecipe.toRecipeCreation(): RecipeCreation =
    RecipeCreation(
        title = recipeDescription.title,
        introduction = recipeDescription.description,
        cookingTime = recipeDescription.cookingTime,
        difficulty = recipeDescription.difficulty,
        ingredients = ingredients.map { it.toIngredient() },
        categories = categories.map { it.categoryName },
        thumbnail = recipeDescription.thumbnail,
        steps = steps.map { it.toRecipeStepMaking() },
    )

fun RecipeCreation.toRecipeCreationRequest(): RecipeCreationRequest =
    RecipeCreationRequest(
        categories = categories,
        cookingTime = cookingTime,
        description = introduction,
        difficulty = difficulty,
        ingredients = ingredients.map { it.toIngredientRequest() },
        recipeSteps = steps.map { it.toRecipeStepRequest() },
        thumbnail = thumbnail,
        title = title,
    )

fun RecipeStepEntity.toRecipeStepMaking(): RecipeStepMaking =
    RecipeStepMaking(
        recipeId = recipeDescriptionId,
        sequence = stepNumber,
        description = description ?: "",
        image = imageTitle ?: "",
        imageUri = imageUri ?: "",
        stepId = id,
        cookingTime = cookingTime ?: "::",
    )

fun IngredientEntity.toIngredient(): Ingredient =
    Ingredient(
        ingredientId = id,
        ingredientName = name,
        requirement = requirement,
    )

fun Ingredient.toIngredientRequest(): IngredientRequest =
    IngredientRequest(
        name = ingredientName,
        requirement = requirement,
    )

fun RecipeStepMaking.toRecipeStepRequest(): RecipeStepRequest =
    RecipeStepRequest(
        cookingTime = cookingTime,
        description = description,
        image = image,
        sequence = sequence,
    )

fun CreatedRecipeDescription.toRecipeDescription(): RecipeDescription =
    RecipeDescription(
        recipeDescriptionId = recipeDescription.id,
        categories = categories.map { it.categoryName },
        cookingTime = recipeDescription.cookingTime,
        description = recipeDescription.description,
        difficulty = recipeDescription.difficulty,
        ingredients = ingredients.map { it.name },
        thumbnail = recipeDescription.thumbnail,
        title = recipeDescription.title,
        imageUri = recipeDescription.imageUri,
    )
