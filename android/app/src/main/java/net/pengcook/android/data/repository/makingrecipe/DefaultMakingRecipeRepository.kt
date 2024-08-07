package net.pengcook.android.data.repository.makingrecipe

import kotlinx.coroutines.flow.first
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.makingrecipe.MakingRecipeRemoteDataSource
import net.pengcook.android.data.util.mapper.toRecipeDescriptionRequest
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import java.io.File

class DefaultMakingRecipeRepository(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val remoteDataSource: MakingRecipeRemoteDataSource,
) : MakingRecipeRepository, NetworkResponseHandler() {
    override suspend fun fetchImageUri(keyName: String): String {
        return remoteDataSource.fetchImageUri(keyName)
    }

    override suspend fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    ) {
        remoteDataSource.uploadImageToS3(presignedUrl, file)
    }

    override suspend fun postRecipeDescription(recipeDescription: RecipeDescription): Result<Long> {
        return runCatching {
            val accessToken =
                sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
            val response =
                remoteDataSource.postRecipeDescription(
                    accessToken,
                    recipeDescription.toRecipeDescriptionRequest(),
                )
            body(response, VALID_POST_CODE).recipeId
        }
    }

    companion object {
        private const val VALID_POST_CODE = 201
    }
}
