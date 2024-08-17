package net.pengcook.android.data.repository.making.step

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.pengcook.android.data.datasource.making.RecipeStepMakingCacheDataSource
import net.pengcook.android.data.datasource.making.RecipeStepMakingLocalDataSource
import net.pengcook.android.data.model.step.RecipeStepEntity
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.RecipeStepMaking

class DefaultRecipeStepMakingRepository(
    private val recipeStepMakingLocalDataSource: RecipeStepMakingLocalDataSource,
    private val recipeStepMakingCacheDataSource: RecipeStepMakingCacheDataSource,
) : NetworkResponseHandler(),
    RecipeStepMakingRepository {
    override suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Result<RecipeStepMaking?> =
        runCatching {
//            val stepFromCache =
//                recipeStepMakingCacheDataSource.fetchRecipeStepByStepNumber(recipeId, sequence)
//                    .getOrNull()
//            if (stepFromCache != null) return@runCatching stepFromCache
            val stepFromDb =
                recipeStepMakingLocalDataSource.fetchRecipeStepByStepNumber(recipeId, sequence)
                    ?.toRecipeStepMaking()
            if (stepFromDb != null) {
                recipeStepMakingCacheDataSource.saveRecipeStep(
                    recipeId = recipeId,
                    recipeStep = stepFromDb,
                )
            }
            stepFromDb
        }

    override suspend fun saveRecipeStep(
        recipeId: Long,
        recipeStep: RecipeStepMaking,
    ): Result<Unit> =
        runCatching {
            val recipeStepEntity = recipeStep.toRecipeStepEntity()
//            recipeStepMakingCacheDataSource.saveRecipeStep(recipeId, recipeStep)
            recipeStepMakingLocalDataSource.insertCreatedRecipeStep(recipeStepEntity)
        }

    override fun deleteRecipeSteps(recipeId: Long) {
        CoroutineScope(Job()).launch {
            recipeStepMakingLocalDataSource.deleteRecipeStepsByRecipeId(recipeId)
        }
    }

    private fun RecipeStepMaking.toRecipeStepEntity(): RecipeStepEntity =
        RecipeStepEntity(
            recipeDescriptionId = recipeId,
            cookingTime = "00:00:00",
            stepNumber = sequence,
            description = description,
            imageUri = imageUri,
            imageTitle = image,
        )

    private fun RecipeStepEntity.toRecipeStepMaking(): RecipeStepMaking =
        RecipeStepMaking(
            stepId = id,
            recipeId = recipeDescriptionId,
            sequence = stepNumber,
            description = description,
            imageUri = imageUri,
            image = imageTitle,
        )

    companion object {
        private const val EXCEPTION_HTTP_CODE = "Http code is not appropriate."
        private const val EXCEPTION_NULL_BODY = "Response body is null."
        private const val RESPONSE_CODE_SUCCESS = 200
    }
}
