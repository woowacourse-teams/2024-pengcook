package net.pengcook.android.data.repository.making

import net.pengcook.android.data.datasource.making.RecipeStepMakingRemoteDataSource
import net.pengcook.android.data.model.step.request.RecipeStepRequest
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.RecipeStep
import net.pengcook.android.presentation.core.model.RecipeStepMaking

class FakeRecipeStepMakingRepository(
    private val recipeStepMakingRemoteDataSource: RecipeStepMakingRemoteDataSource,
) : NetworkResponseHandler(),
    RecipeStepMakingRepository {
    override suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Result<RecipeStepMaking?> = TODO()

    override suspend fun saveRecipeStep(
        recipeId: Long,
        recipeStep: RecipeStepMaking,
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun deleteRecipeSteps(recipeId: Long) {
        TODO("Not yet implemented")
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
