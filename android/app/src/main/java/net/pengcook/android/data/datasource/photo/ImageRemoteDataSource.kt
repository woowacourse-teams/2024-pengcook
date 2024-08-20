package net.pengcook.android.data.datasource.photo

import net.pengcook.android.data.model.PresignedUrlResponse
import retrofit2.Response
import java.io.File

interface ImageRemoteDataSource {
    suspend fun fetchImageUri(keyName: String): Response<PresignedUrlResponse>

    suspend fun uploadImage(
        presignedUrl: String,
        file: File,
    ): Response<Unit>
}
