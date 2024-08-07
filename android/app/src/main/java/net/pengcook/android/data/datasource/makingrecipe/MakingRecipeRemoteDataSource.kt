package net.pengcook.android.data.datasource.makingrecipe

import net.pengcook.android.data.model.makingrecipe.RecipeDescriptionRequest
import net.pengcook.android.data.model.makingrecipe.RecipeDescriptionResponse
import retrofit2.Response
import java.io.File

interface MakingRecipeRemoteDataSource {
    suspend fun fetchImageUri(keyName: String): String

    suspend fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    )

    suspend fun postRecipeDescription(
        accessToken: String,
        recipeDescriptionRequest: RecipeDescriptionRequest,
    ): Response<RecipeDescriptionResponse>
}
