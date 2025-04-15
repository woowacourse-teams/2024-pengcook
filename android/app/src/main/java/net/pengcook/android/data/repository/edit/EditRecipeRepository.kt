package net.pengcook.android.data.repository.edit

import net.pengcook.android.data.datasource.edit.EditRecipeCacheDataSource
import net.pengcook.android.data.datasource.edit.SavedIngredient
import net.pengcook.android.data.datasource.edit.SavedRecipeDescription
import net.pengcook.android.data.datasource.edit.SavedRecipeSteps
import net.pengcook.android.domain.model.recipemaking.RecipeCreation
import net.pengcook.android.presentation.core.model.Ingredient
import net.pengcook.android.presentation.core.model.RecipeStepMaking

object EditRecipeRepository {
    fun saveRecipe(recipeCreation: RecipeCreation): Result<Unit> =
        runCatching {
            EditRecipeCacheDataSource.saveRecipeDescription(recipeCreation.toSavedRecipeDescription())
            EditRecipeCacheDataSource.saveRecipeSteps(recipeCreation.steps.map { it.toSavedRecipeStep() })
        }

    fun saveRecipeSteps(recipeSteps: List<RecipeStepMaking>): Result<Unit> =
        runCatching {
            EditRecipeCacheDataSource.saveRecipeSteps(recipeSteps.map { it.toSavedRecipeStep() })
        }

    fun saveRecipeDescription(recipeDescription: RecipeCreation): Result<Unit> =
        runCatching {
            EditRecipeCacheDataSource.saveRecipeDescription(recipeDescription.toSavedRecipeDescription())
        }

    fun fetchAllSavedRecipeData(): Result<RecipeCreation> =
        runCatching {
            val description = EditRecipeCacheDataSource.fetchSavedRecipeDescription()
            val steps = EditRecipeCacheDataSource.savedRecipeSteps
            RecipeCreation(
                title = description.title,
                introduction = description.description,
                cookingTime = description.cookingTime,
                difficulty = description.difficulty,
                ingredients =
                    description.ingredients.map {
                        Ingredient(
                            ingredientId = 1L,
                            ingredientName = it.name,
                            requirement = it.requirement,
                        )
                    },
                categories = description.categories,
                thumbnail = description.thumbnail,
                steps =
                    steps.mapIndexed { index, it ->
                        RecipeStepMaking(
                            stepId = index.toLong(),
                            recipeId = description.id,
                            description = it.description ?: "",
                            image = it.imageUri ?: "",
                            sequence = it.sequence,
                            imageUri = it.imageUri ?: "",
                            cookingTime = it.cookingTime ?: "00:00:00",
                            imageUploaded = it.imageUploaded,
                        )
                    },
            )
        }

//    fun fetchSavedRecipeSteps(): Result<List<RecipeStep>> = runCatching {
//        EditRecipeCacheDataSource.savedRecipeSteps.map {
//            RecipeStep(
//                stepId = it.sequence.toLong(),
//                description = it.description ?: "",
//                image = it.imageUri ?: "",
//                sequence = it.sequence,
//                image = it.imageUri ?: "",
//                cookingTime = it.cookingTime ?: "00:00:00",
//                imageUploaded = it.imageUploaded,
//            )
//        }
//    }
//

    fun clearSavedRecipeData() {
        EditRecipeCacheDataSource.clearSavedRecipeDescription()
        EditRecipeCacheDataSource.clearSavedRecipeSteps()
    }

    private fun RecipeCreation.toSavedRecipeDescription(): SavedRecipeDescription =
        SavedRecipeDescription(
            id = recipeId,
            title = title,
            imageUri = "",
            description = introduction,
            cookingTime = cookingTime,
            categories = categories,
            ingredients =
                ingredients.map {
                    SavedIngredient(
                        name = it.ingredientName,
                    )
                },
            difficulty = difficulty,
            thumbnail = thumbnail,
        )

    private fun RecipeStepMaking.toSavedRecipeStep(): SavedRecipeSteps =
        SavedRecipeSteps(
            sequence = sequence,
            imageUri = imageUri,
            imageTitle = "",
            cookingTime = cookingTime,
            description = description,
            imageUploaded = imageUploaded,
        )
}
