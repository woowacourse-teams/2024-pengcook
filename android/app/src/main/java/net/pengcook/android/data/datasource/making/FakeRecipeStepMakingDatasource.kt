package net.pengcook.android.data.datasource.making

import net.pengcook.android.data.model.step.RecipeStepResponse
import retrofit2.Response

class FakeRecipeStepMakingDatasource : RecipeStepMakingDataSource {
    private val recipeSteps =
        mutableListOf(
            RecipeStepResponse(
                stepId = 1,
                recipeId = 1,
                sequence = 1,
                description = "description1",
                image = "https://static.wtable.co.kr/image/production/service/recipe/291/a9bb71ac-3eed-447c-baf4-47cd68bd4e43.jpg",
            ),
            RecipeStepResponse(
                stepId = 2,
                recipeId = 1,
                sequence = 2,
                description = "description2",
                image = "https://static.wtable.co.kr/image/production/service/recipe/291/30ef8cd6-3d65-4fbb-88c1-14aac9e6f393.jpg",
            ),
            RecipeStepResponse(
                stepId = 3,
                recipeId = 1,
                sequence = 3,
                description = "description3",
                image = "https://static.wtable.co.kr/image/production/service/recipe/291/84fc1319-8e86-407f-a19c-90215165e6ab.jpg",
            ),
            RecipeStepResponse(
                stepId = 4,
                recipeId = 1,
                sequence = 4,
                description = "description4",
                image = "",
            ),
        )

    override suspend fun fetchRecipeStep(
        recipeId: Long,
        sequence: Int,
    ): Response<RecipeStepResponse> = Response.success(recipeSteps.find { it.recipeId == recipeId && it.sequence == sequence })

    override suspend fun uploadRecipeStep(recipeStepResponse: RecipeStepResponse): Response<Unit> {
        if (recipeSteps.find { it.recipeId == recipeStepResponse.recipeId && it.sequence == recipeStepResponse.sequence } != null) {
            recipeSteps.removeIf { it.recipeId == recipeStepResponse.recipeId && it.sequence == recipeStepResponse.sequence }
        }
        recipeSteps.add(recipeStepResponse)
        return Response.success(Unit)
    }
}