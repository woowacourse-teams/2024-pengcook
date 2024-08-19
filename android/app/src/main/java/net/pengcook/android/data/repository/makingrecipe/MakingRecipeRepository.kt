package net.pengcook.android.data.repository.makingrecipe

import net.pengcook.android.domain.model.recipemaking.RecipeCreation
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import java.io.File

interface MakingRecipeRepository {
    suspend fun fetchImageUri(keyName: String): String

    suspend fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    )

    suspend fun fetchTotalRecipeData(): Result<RecipeCreation?>

    suspend fun fetchRecipeDescription(): Result<RecipeDescription?>

    suspend fun saveRecipeDescription(recipeDescription: RecipeDescription): Result<Long>

    suspend fun postNewRecipe(newRecipe: RecipeCreation): Result<Long>

    fun deleteRecipeDescription(recipeId: Long)
}
