package net.pengcook.android.data.repository.making.step

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.pengcook.android.data.datasource.making.RecipeStepMakingCacheDataSource
import net.pengcook.android.data.datasource.making.RecipeStepMakingLocalDataSource
import net.pengcook.android.data.model.step.RecipeStepEntity
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.RecipeStepMaking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRecipeStepMakingRepository
    @Inject
    constructor(
        private val recipeStepMakingLocalDataSource: RecipeStepMakingLocalDataSource,
        private val recipeStepMakingCacheDataSource: RecipeStepMakingCacheDataSource,
    ) : NetworkResponseHandler(),
        RecipeStepMakingRepository {
        override suspend fun fetchRecipeStepsByRecipeId(recipeId: Long): Result<List<RecipeStepMaking>?> {
            return runCatching {
                recipeStepMakingLocalDataSource.fetchRecipeStepsByRecipeId(recipeId)?.map {
                    it.toRecipeStepMaking()
                }
            }
        }

        override suspend fun fetchRecipeStep(
            recipeId: Long,
            sequence: Int,
        ): Result<RecipeStepMaking?> =
            runCatching {
                val stepFromDb =
                    recipeStepMakingLocalDataSource
                        .fetchRecipeStepByStepNumber(recipeId, sequence)
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
                cookingTime = cookingTime,
                stepNumber = sequence,
                description = description.ifEmpty { null },
                imageUri = imageUri.ifEmpty { null },
                imageTitle = image.ifEmpty { null },
            )

        private fun RecipeStepEntity.toRecipeStepMaking(): RecipeStepMaking =
            RecipeStepMaking(
                stepId = id,
                recipeId = recipeDescriptionId,
                sequence = stepNumber,
                description = description ?: "",
                imageUri = imageUri ?: "",
                image = imageTitle ?: "",
                cookingTime = cookingTime ?: "00:00:00",
            )
    }
