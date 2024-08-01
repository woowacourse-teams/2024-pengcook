package net.pengcook.android.data.datasource.makingrecipe

import java.io.File

interface MakingRecipeRemoteDataSource {
    suspend fun fetchImageUri(keyName: String): String

    suspend fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    )
}
