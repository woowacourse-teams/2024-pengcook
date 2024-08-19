package net.pengcook.android.data.datasource.making

import net.pengcook.android.presentation.core.model.RecipeStepMaking

class DefaultRecipeStepMakingCacheDataSource : RecipeStepMakingCacheDataSource {
    private var recipeSteps: Map<Int, RecipeStepMaking> = emptyMap()

    override suspend fun fetchRecipeStepByStepNumber(
        recipeId: Long,
        sequence: Int,
    ): Result<RecipeStepMaking?> {
        return runCatching { recipeSteps[sequence] }
    }

    override suspend fun saveRecipeStep(
        recipeId: Long,
        recipeStep: RecipeStepMaking,
    ): Result<Unit> {
        return runCatching {
            recipeSteps =
                recipeSteps.toMutableMap().apply {
                    this[recipeStep.sequence] = recipeStep
                }
        }
    }

    override suspend fun deleteRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Result<Unit> {
        return runCatching {
            recipeSteps =
                recipeSteps.toMutableMap().apply {
                    this.remove(sequence)
                }
        }
    }
}
