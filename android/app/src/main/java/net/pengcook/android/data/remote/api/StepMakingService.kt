package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.makingrecipe.request.RecipeStepRequest
import net.pengcook.android.data.model.step.RecipeStepResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StepMakingService {
    @GET("/recipes/{recipeId}/steps/{sequence}")
    suspend fun fetchRecipeSteps(
        @Path("recipeId") recipeId: Long,
        @Path("sequence") sequence: Int,
    ): Response<RecipeStepResponse>

    @POST("/recipes/{recipeId}/steps")
    suspend fun uploadRecipeStep(
        @Path("recipeId") recipeId: Long,
        @Body recipeStepRequest: RecipeStepRequest,
    ): Response<Unit>
}
