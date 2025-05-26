package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.model.makingrecipe.request.RecipeStepRequest
import net.pengcook.android.data.model.step.RecipeStepResponse
import retrofit2.Response

class FakeRecipeStepMakingRemoteDatasource : RecipeStepMakingRemoteDataSource {
    private val recipeSteps =
        mutableListOf(
            RecipeStepResponse(
                id = 1,
                recipeId = 1,
                sequence = 1,
                description = "description1",
                image = "https://static.wtable.co.kr/image/production/service/recipe/291/a9bb71ac-3eed-447c-baf4-47cd68bd4e43.jpg",
                cookingTime = "114325",
            ),
            RecipeStepResponse(
                id = 2,
                recipeId = 1,
                sequence = 2,
                description = "description2",
                image = "https://static.wtable.co.kr/image/production/service/recipe/291/30ef8cd6-3d65-4fbb-88c1-14aac9e6f393.jpg",
                cookingTime = "114325",
            ),
            RecipeStepResponse(
                id = 3,
                recipeId = 1,
                sequence = 3,
                description = "description3",
                image = "https://static.wtable.co.kr/image/production/service/recipe/291/84fc1319-8e86-407f-a19c-90215165e6ab.jpg",
                cookingTime = "114325",
            ),
            RecipeStepResponse(
                id = 4,
                recipeId = 1,
                sequence = 4,
                description = "description4",
                image = "",
                cookingTime = "114325",
            ),
        )

    override suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Response<RecipeStepResponse> = Response.success(recipeSteps.find { it.recipeId == recipeId && it.sequence == sequence })

    suspend fun uploadRecipeStep(
        recipeId: Long,
        recipeStepRequest: RecipeStepRequest,
    ): Response<Unit> {
        if (recipeSteps.find { it.recipeId == recipeId && it.sequence == recipeStepRequest.sequence } != null) {
            recipeSteps.removeIf { it.recipeId == recipeId && it.sequence == recipeStepRequest.sequence }
        }
        recipeSteps.add(
            RecipeStepResponse(
                id = recipeSteps.size + 1L,
                recipeId = recipeId,
                sequence = recipeStepRequest.sequence,
                description = recipeStepRequest.description,
                image = recipeStepRequest.image,
                cookingTime = recipeStepRequest.cookingTime,
            ),
        )
        return Response.success(Unit)
    }
}
