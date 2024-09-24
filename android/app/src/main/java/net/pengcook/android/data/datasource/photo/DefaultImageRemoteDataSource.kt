package net.pengcook.android.data.datasource.photo

import net.pengcook.android.data.model.PresignedUrlResponse
import net.pengcook.android.data.remote.api.ImageService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class
DefaultImageRemoteDataSource@Inject
    constructor(
        private val imageService: ImageService,
    ) : ImageRemoteDataSource {
        override suspend fun fetchImageUri(keyName: String): Response<PresignedUrlResponse> {
            return imageService.fetchImageUri(keyName)
        }

        override suspend fun uploadImage(
            presignedUrl: String,
            file: File,
        ): Response<Unit> {
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            return imageService.uploadImage(presignedUrl, requestFile)
        }
    }
