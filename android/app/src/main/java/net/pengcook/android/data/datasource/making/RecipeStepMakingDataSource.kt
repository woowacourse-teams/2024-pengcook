package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.model.step.RecipeStepResponse
import retrofit2.Response

interface RecipeStepMakingDataSource {
    suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Response<RecipeStepResponse>

    suspend fun uploadRecipeStep(recipeStepResponse: RecipeStepResponse): Response<Unit>
}
