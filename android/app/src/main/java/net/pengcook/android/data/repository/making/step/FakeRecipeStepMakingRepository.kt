package net.pengcook.android.data.repository.making.step

import net.pengcook.android.data.datasource.making.RecipeStepMakingDataSource
import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.util.mapper.toRecipeStep
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.RecipeStep
import retrofit2.Response

class FakeRecipeStepMakingRepository(
    private val recipeStepMakingDataSource: RecipeStepMakingDataSource,
) : RecipeStepMakingRepository,
    NetworkResponseHandler {
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

    override fun <T> body(
        response: Response<T>,
        validHttpCode: Int,
    ): T {
        val code = response.code()
        val body = response.body()
        if (code != validHttpCode) throw RuntimeException(EXCEPTION_HTTP_CODE)
        if (body == null) throw RuntimeException(EXCEPTION_NULL_BODY)
        return body
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
