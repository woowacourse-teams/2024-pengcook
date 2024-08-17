package net.pengcook.android.data.repository.making.step

import net.pengcook.android.presentation.core.model.RecipeStepMaking

interface RecipeStepMakingRepository {
    suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Result<RecipeStepMaking?>

    suspend fun saveRecipeStep(
        recipeId: Long,
        recipeStep: RecipeStepMaking,
    ): Result<Unit>

    fun deleteRecipeSteps(recipeId: Long)
}
