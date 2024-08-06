package net.pengcook.android.data.repository.making.step

import net.pengcook.android.data.datasource.making.RecipeStepMakingDataSource
import net.pengcook.android.data.model.step.request.RecipeStepRequest
import net.pengcook.android.data.util.mapper.toRecipeStep
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.RecipeStep

class FakeRecipeStepMakingRepository(
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

    override suspend fun uploadRecipeStep(
        recipeId: Long,
        recipeStep: RecipeStep,
    ): Result<Unit> =
        runCatching {
            val response = recipeStepMakingDataSource.uploadRecipeStep(recipeId, recipeStep.toRecipeStepRequest())
            body(response, RESPONSE_CODE_SUCCESS)
        }

    private fun RecipeStep.toRecipeStepRequest(): RecipeStepRequest =
        RecipeStepRequest(
            image = image,
            description = description,
            sequence = sequence,
            cookingTime = "00:05:00",
        )

    companion object {
        private const val EXCEPTION_HTTP_CODE = "Http code is not appropriate."
        private const val EXCEPTION_NULL_BODY = "Response body is null."
        private const val RESPONSE_CODE_SUCCESS = 200
    }
}
