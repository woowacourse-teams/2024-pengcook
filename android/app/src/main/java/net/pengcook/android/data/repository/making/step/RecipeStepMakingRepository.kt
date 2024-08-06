package net.pengcook.android.data.repository.making.step

import net.pengcook.android.presentation.core.model.RecipeStep

interface RecipeStepMakingRepository {
    suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Result<RecipeStep>

    suspend fun uploadRecipeStep(
        recipeId: Long,
        recipeStep: RecipeStep,
    ): Result<Unit>
}
