package net.pengcook.android.data.repository.makingrecipe

import net.pengcook.android.data.datasource.makingrecipe.MakingRecipeRemoteDataSource
import java.io.File

class MakingRecipeRepository(private val remoteDataSource: MakingRecipeRemoteDataSource) {

    suspend fun fetchImageUri(keyName: String): String {
        return remoteDataSource.fetchImageUri(keyName)
    }

    suspend fun uploadImageToS3(presignedUrl: String, file: File) {
        remoteDataSource.uploadImageToS3(presignedUrl, file)
    }
}
