package net.pengcook.android.data.datasource.making

import net.pengcook.android.presentation.core.model.RecipeStepMaking

interface RecipeStepMakingCacheDataSource {
    suspend fun fetchRecipeStepByStepNumber(
        recipeId: Long,
        sequence: Int,
    ): Result<RecipeStepMaking?>

    suspend fun saveRecipeStep(
        recipeId: Long,
        recipeStep: RecipeStepMaking,
    ): Result<Unit>

    suspend fun deleteRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Result<Unit>
}
