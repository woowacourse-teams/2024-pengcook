package net.pengcook.android.data.repository.making.step

import net.pengcook.android.data.datasource.making.RecipeStepMakingDataSource
import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.util.mapper.toRecipeStep
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.RecipeStep

class DefaultRecipeStepMakingRepository(
    private val recipeStepMakingDataSource: RecipeStepMakingDataSource,
) : NetworkResponseHandler(),
    RecipeStepMakingRepository {
    override suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Result<RecipeStep> =
        runCatching {
            val response = recipeStepMakingDataSource.fetchRecipeStep(recipeId, sequence)
            body(response, RESPONSE_CODE_SUCCESS).toRecipeStep()
        }

    override suspend fun uploadRecipeStep(recipeStep: RecipeStep): Result<Unit> =
        runCatching {
            val response = recipeStepMakingDataSource.uploadRecipeStep(recipeStep.toRecipeStepResponse())
            body(response, RESPONSE_CODE_SUCCESS)
        }

    private fun RecipeStep.toRecipeStepResponse(): RecipeStepResponse =
        RecipeStepResponse(
            stepId = stepId,
            recipeId = recipeId,
            sequence = sequence,
            image = image,
            description = description,
        )

    companion object {
        private const val EXCEPTION_HTTP_CODE = "Http code is not appropriate."
        private const val EXCEPTION_NULL_BODY = "Response body is null."
        private const val RESPONSE_CODE_SUCCESS = 200
    }
}
