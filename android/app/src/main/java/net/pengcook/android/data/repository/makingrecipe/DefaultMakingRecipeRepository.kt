package net.pengcook.android.data.repository.makingrecipe

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.makingrecipe.MakingRecipeLocalDataSource
import net.pengcook.android.data.datasource.makingrecipe.MakingRecipeRemoteDataSource
import net.pengcook.android.data.model.makingrecipe.entity.CategoryEntity
import net.pengcook.android.data.util.mapper.toIngredientEntities
import net.pengcook.android.data.util.mapper.toRecipeCreation
import net.pengcook.android.data.util.mapper.toRecipeCreationRequest
import net.pengcook.android.data.util.mapper.toRecipeDescription
import net.pengcook.android.data.util.mapper.toRecipeDescriptionEntity
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.domain.model.recipemaking.RecipeCreation
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMakingRecipeRepository
    @Inject
    constructor(
        private val sessionLocalDataSource: SessionLocalDataSource,
        private val makingRecipeRemoteDataSource: MakingRecipeRemoteDataSource,
        private val makingRecipeLocalDataSource: MakingRecipeLocalDataSource,
    ) : NetworkResponseHandler(),
        MakingRecipeRepository {
        override suspend fun fetchImageUri(keyName: String): String = makingRecipeRemoteDataSource.fetchImageUri(keyName)

        override suspend fun uploadImageToS3(
            presignedUrl: String,
            file: File,
        ) {
            makingRecipeRemoteDataSource.uploadImageToS3(presignedUrl, file)
        }

        override suspend fun fetchTotalRecipeData(): Result<RecipeCreation?> =
            runCatching {
                val recipeCreation =
                    makingRecipeLocalDataSource.fetchTotalRecipeData()?.toRecipeCreation()
                println(recipeCreation)
                recipeCreation
            }

        override suspend fun fetchRecipeDescription(): Result<RecipeDescription?> =
            runCatching {
                makingRecipeLocalDataSource.fetchRecipeDescription()?.toRecipeDescription()
            }

        override suspend fun saveRecipeDescription(recipeDescription: RecipeDescription): Result<Long> =
            runCatching {
                val id = recipeDescription.recipeDescriptionId
                val recipeDescriptionEntity = recipeDescription.toRecipeDescriptionEntity(id)
                val categoryEntity =
                    CategoryEntity(
                        recipeId = id,
                        categoryName = recipeDescription.categories.joinToString(",") { it.trim() },
                    )
                val ingredientEntities = recipeDescription.ingredients.toIngredientEntities(id)
                makingRecipeLocalDataSource.saveRecipeDescription(
                    recipeDescription = recipeDescriptionEntity,
                    ingredients = ingredientEntities,
                    category = categoryEntity,
                )
            }

        override suspend fun postNewRecipe(newRecipe: RecipeCreation): Result<Long> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken
                        ?: throw RuntimeException()
                val request = newRecipe.toRecipeCreationRequest()
                val response = makingRecipeRemoteDataSource.uploadNewRecipe(accessToken, request)
                body(response, VALID_POST_CODE).recipeId
            }

        override fun deleteRecipeDescription(recipeId: Long) {
            CoroutineScope(Job()).launch {
                makingRecipeLocalDataSource.deleteCreatedRecipeById(recipeId)
            }
        }

        companion object {
            private const val VALID_POST_CODE = 201
        }
    }
