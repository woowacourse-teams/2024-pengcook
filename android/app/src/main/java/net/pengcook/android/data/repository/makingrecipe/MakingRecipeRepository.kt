package net.pengcook.android.data.repository.makingrecipe

import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import java.io.File

interface MakingRecipeRepository {
    suspend fun fetchImageUri(keyName: String): String

    suspend fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    )

    suspend fun postRecipeDescription(recipeDescription: RecipeDescription): Result<Long>
}
