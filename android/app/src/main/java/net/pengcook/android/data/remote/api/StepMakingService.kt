package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.step.RecipeStepResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StepMakingService {
    @GET("/api/recipes/{recipeId}/steps/{sequence}")
    suspend fun fetchRecipeSteps(
        @Query("recipeId") recipeId: Long,
        @Query("sequence") sequence: Int,
    ): Response<RecipeStepResponse>
}
