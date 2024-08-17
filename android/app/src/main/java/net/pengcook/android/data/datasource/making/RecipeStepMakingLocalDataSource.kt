package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.model.step.RecipeStepEntity

interface RecipeStepMakingLocalDataSource {
    suspend fun insertCreatedRecipeStep(recipeStep: RecipeStepEntity): Long

    suspend fun fetchRecipeStepsByRecipeId(recipeId: Long): List<RecipeStepEntity>?

    suspend fun fetchRecipeStepByStepNumber(
        recipeId: Long,
        stepNumber: Int,
    ): RecipeStepEntity?

    suspend fun deleteRecipeStepsByRecipeId(recipeId: Long)
}
