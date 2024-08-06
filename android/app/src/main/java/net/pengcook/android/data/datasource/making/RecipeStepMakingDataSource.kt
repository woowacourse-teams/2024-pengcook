package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.model.step.request.RecipeStepRequest
import retrofit2.Response

interface RecipeStepMakingDataSource {
    suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Response<RecipeStepResponse>

    suspend fun uploadRecipeStep(
        recipeId: Long,
        recipeStepRequest: RecipeStepRequest,
    ): Response<Unit>
}
