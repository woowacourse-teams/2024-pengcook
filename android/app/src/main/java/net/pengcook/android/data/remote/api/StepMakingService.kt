package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.step.RecipeStepResponse
import net.pengcook.android.data.model.step.request.RecipeStepRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StepMakingService {
    @GET("/api/recipes/{recipeId}/steps/{sequence}")
    suspend fun fetchRecipeSteps(
        @Path("recipeId") recipeId: Long,
        @Path("sequence") sequence: Int,
    ): Response<RecipeStepResponse>

    @POST("/api/recipees/{recipeId}/steps")
    suspend fun uploadRecipeStep(
        @Path("recipeId") recipeId: Long,
        @Body recipeStepRequest: RecipeStepRequest,
    ): Response<Unit>
}
