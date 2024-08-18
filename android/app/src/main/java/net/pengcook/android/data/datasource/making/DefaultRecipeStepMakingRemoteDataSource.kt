package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.remote.api.StepMakingService
import retrofit2.Response

class DefaultRecipeStepMakingRemoteDataSource(
    private val stepMakingService: StepMakingService,
) : RecipeStepMakingRemoteDataSource {
    override suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Response<RecipeStepResponse> = stepMakingService.fetchRecipeSteps(recipeId, sequence)
}
