package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.model.step.request.RecipeStepRequest
import net.pengcook.android.data.remote.api.StepMakingService
import retrofit2.Response

class DefaultRecipeStepMakingDataSource(
    private val stepMakingService: StepMakingService,
) : RecipeStepMakingDataSource {
    override suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Response<RecipeStepResponse> = stepMakingService.fetchRecipeSteps(recipeId, sequence)

    override suspend fun uploadRecipeStep(
        recipeId: Long,
        recipeStepRequest: RecipeStepRequest,
    ): Response<Unit> =
        stepMakingService.uploadRecipeStep(
            recipeId = recipeId,
            recipeStepRequest = recipeStepRequest,
        )
}
