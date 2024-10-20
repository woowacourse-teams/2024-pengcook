package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.local.database.dao.RecipeStepDao
import net.pengcook.android.data.model.step.RecipeStepEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRecipeStepMakingLocalDataSource
    @Inject
    constructor(
        private val recipeStepDao: RecipeStepDao,
    ) : RecipeStepMakingLocalDataSource {
        override suspend fun insertCreatedRecipeStep(recipeStep: RecipeStepEntity): Long {
            return recipeStepDao.insertCreatedRecipeStep(recipeStep)
        }

        override suspend fun fetchRecipeStepsByRecipeId(): List<RecipeStepEntity>? {
            return recipeStepDao.fetchRecipeStepsByRecipeId()
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

        override suspend fun deleteRecipeStepByStepNumber(
            recipeId: Long,
            stepNumber: Int,
        ) {
            recipeStepDao.deleteRecipeStepByStepNumber(recipeId, stepNumber)
        }

        override suspend fun updateRecipeStepImage(
            id: Long,
            imageUri: String?,
            imageTitle: String?,
            imageUploaded: Boolean,
        ) {
            recipeStepDao.updateRecipeStepImage(id, imageUri, imageTitle, imageUploaded)
        }
    }
