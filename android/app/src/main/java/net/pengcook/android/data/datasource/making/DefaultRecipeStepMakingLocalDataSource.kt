package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.local.database.dao.RecipeStepDao
import net.pengcook.android.data.model.step.RecipeStepEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRecipeStepMakingLocalDataSource @Inject constructor(
    private val recipeStepDao: RecipeStepDao,
) : RecipeStepMakingLocalDataSource {
    override suspend fun insertCreatedRecipeStep(recipeStep: RecipeStepEntity): Long {
        return recipeStepDao.insertCreatedRecipeStep(recipeStep)
    }

    override suspend fun fetchRecipeStepsByRecipeId(recipeId: Long): List<RecipeStepEntity>? {
        return recipeStepDao.fetchRecipeStepsByRecipeId(recipeId)
    }

    override suspend fun fetchRecipeStepByStepNumber(
        recipeId: Long,
        stepNumber: Int,
    ): RecipeStepEntity? {
        return recipeStepDao.fetchRecipeStepByStepNumber(recipeId, stepNumber)
    }

    override suspend fun deleteRecipeStepsByRecipeId(recipeId: Long) {
        recipeStepDao.deleteRecipeStepsByRecipeId(recipeId)
    }
}
