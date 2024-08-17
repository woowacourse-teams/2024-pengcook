package net.pengcook.android.data.repository.makingrecipe

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.makingrecipe.MakingRecipeLocalDataSource
import net.pengcook.android.data.datasource.makingrecipe.MakingRecipeRemoteDataSource
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipe
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipeDescription
import net.pengcook.android.data.model.makingrecipe.entity.IngredientEntity
import net.pengcook.android.data.model.makingrecipe.request.IngredientRequest
import net.pengcook.android.data.model.makingrecipe.request.RecipeCreationRequest
import net.pengcook.android.data.model.makingrecipe.request.RecipeStepRequest
import net.pengcook.android.data.model.step.RecipeStepEntity
import net.pengcook.android.data.util.mapper.toCategoryEntities
import net.pengcook.android.data.util.mapper.toIngredientEntities
import net.pengcook.android.data.util.mapper.toRecipeDescriptionEntity
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.domain.model.recipemaking.RecipeCreation
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import net.pengcook.android.presentation.core.model.Ingredient
import net.pengcook.android.presentation.core.model.RecipeStepMaking
import java.io.File

class DefaultMakingRecipeRepository(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val makingRecipeRemoteDataSource: MakingRecipeRemoteDataSource,
    private val makingRecipeLocalDataSource: MakingRecipeLocalDataSource,
) : MakingRecipeRepository, NetworkResponseHandler() {
    override suspend fun fetchImageUri(keyName: String): String {
        return makingRecipeRemoteDataSource.fetchImageUri(keyName)
    }

    override suspend fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    ) {
        makingRecipeRemoteDataSource.uploadImageToS3(presignedUrl, file)
    }

    override suspend fun fetchTotalRecipeData(): Result<RecipeCreation?> {
        return runCatching {
            makingRecipeLocalDataSource.fetchTotalRecipeData()?.toRecipeCreation()
        }
    }

    override suspend fun fetchRecipeDescription(): Result<RecipeDescription?> {
        return runCatching {
            makingRecipeLocalDataSource.fetchRecipeDescription()?.toRecipeDescription()
        }
    }

    override suspend fun saveRecipeDescription(recipeDescription: RecipeDescription): Result<Long> {
        return runCatching {
            val id = recipeDescription.recipeDescriptionId
            val recipeDescriptionEntity = recipeDescription.toRecipeDescriptionEntity(id)
            val categoryEntities = recipeDescription.categories.toCategoryEntities(id)
            val ingredientEntities = recipeDescription.ingredients.toIngredientEntities(id)
            makingRecipeLocalDataSource.saveRecipeDescription(
                recipeDescription = recipeDescriptionEntity,
                ingredients = ingredientEntities,
                categories = categoryEntities,
            )
        }
    }

    override suspend fun postNewRecipe(newRecipe: RecipeCreation): Result<Long> {
        return runCatching {
            val accessToken =
                sessionLocalDataSource.sessionData.first().accessToken
                    ?: throw RuntimeException()
            val request = newRecipe.toRecipeCreationRequest()
            val response = makingRecipeRemoteDataSource.uploadNewRecipe(accessToken, request)
            body(response, VALID_POST_CODE).recipeId
        }
    }

    override fun deleteRecipeDescription(recipeId: Long) {
        CoroutineScope(Job()).launch {
            makingRecipeLocalDataSource.deleteCreatedRecipeById(recipeId)
        }
    }

    private fun CreatedRecipe.toRecipeCreation(): RecipeCreation =
        RecipeCreation(
            title = recipeDescription.title,
            introduction = recipeDescription.description,
            cookingTime = recipeDescription.cookingTime,
            difficulty = recipeDescription.difficulty,
            ingredients = ingredients.map { it.toIngredient() },
            categories = categories.map { it.categoryName },
            thumbnail = recipeDescription.thumbnail,
            steps = steps.map { it.toRecipeStepMaking() },
        )

    private fun RecipeCreation.toRecipeCreationRequest(): RecipeCreationRequest =
        RecipeCreationRequest(
            categories = categories,
            cookingTime = cookingTime,
            description = introduction,
            difficulty = difficulty,
            ingredients = ingredients.map { it.toIngredientRequest() },
            recipeSteps = steps.map { it.toRecipeStepRequest() },
            thumbnail = thumbnail,
            title = title,
        )

    private fun RecipeStepEntity.toRecipeStepMaking(): RecipeStepMaking =
        RecipeStepMaking(
            recipeId = recipeDescriptionId,
            sequence = stepNumber,
            description = description,
            image = imageTitle,
            imageUri = imageUri,
            stepId = id,
        )

    private fun IngredientEntity.toIngredient(): Ingredient =
        Ingredient(
            ingredientId = id,
            ingredientName = name,
            requirement = requirement,
        )

    private fun Ingredient.toIngredientRequest(): IngredientRequest =
        IngredientRequest(
            name = ingredientName,
            requirement = requirement,
        )

    private fun RecipeStepMaking.toRecipeStepRequest(): RecipeStepRequest =
        RecipeStepRequest(
            cookingTime = "00:00:00",
            description = description,
            image = image,
            sequence = sequence,
        )

    private fun CreatedRecipeDescription.toRecipeDescription(): RecipeDescription =
        RecipeDescription(
            recipeDescriptionId = recipeDescription.id,
            categories = categories.map { it.categoryName },
            cookingTime = recipeDescription.cookingTime,
            description = recipeDescription.description,
            difficulty = recipeDescription.difficulty,
            ingredients = ingredients.map { it.name },
            thumbnail = recipeDescription.thumbnail,
            title = recipeDescription.title,
            imageUri = recipeDescription.imageUri,
        )

    companion object {
        private const val VALID_POST_CODE = 201
    }
}
