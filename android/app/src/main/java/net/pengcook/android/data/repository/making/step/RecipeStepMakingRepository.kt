package net.pengcook.android.data.repository.making.step

import net.pengcook.android.presentation.core.model.RecipeStepMaking

interface RecipeStepMakingRepository {
    suspend fun fetchRecipeStepsByRecipeId(): Result<List<RecipeStepMaking>?>

    suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Result<RecipeStepMaking?>

    suspend fun saveRecipeStep(
        recipeId: Long,
        recipeStep: RecipeStepMaking,
    ): Result<Unit>

    fun deleteRecipeSteps(recipeId: Long)

    suspend fun deleteRecipeStepsNonAsync(recipeId: Long)

    suspend fun updateRecipeStepImage(
        id: Long,
        imageUri: String?,
        imageTitle: String?,
        imageUploaded: Boolean,
    )

    suspend fun deleteRecipeStepByStepNumber(
        recipeId: Long,
        stepNumber: Int,
    )
}
