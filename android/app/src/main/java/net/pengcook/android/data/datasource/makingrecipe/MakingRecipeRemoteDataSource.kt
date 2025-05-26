package net.pengcook.android.data.datasource.makingrecipe

import net.pengcook.android.data.model.makingrecipe.RecipeCreationResponse
import net.pengcook.android.data.model.makingrecipe.request.RecipeCreationRequest
import retrofit2.Response
import java.io.File

interface MakingRecipeRemoteDataSource {
    suspend fun fetchImageUri(keyName: String): String

    suspend fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    )

    suspend fun uploadNewRecipe(
        accessToken: String,
        newRecipe: RecipeCreationRequest,
    ): Response<RecipeCreationResponse>
}
