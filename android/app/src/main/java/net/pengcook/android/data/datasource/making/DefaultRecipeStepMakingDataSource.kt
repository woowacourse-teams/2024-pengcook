package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.remote.api.StepMakingService
import retrofit2.Response

class DefaultRecipeStepMakingDataSource(
    private val stepMakingService: StepMakingService,
) : RecipeStepMakingDataSource {
    override suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Response<RecipeStepResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadRecipeStep(recipeStepResponse: RecipeStepResponse): Response<Unit> {
        TODO("Not yet implemented")
    }
}
