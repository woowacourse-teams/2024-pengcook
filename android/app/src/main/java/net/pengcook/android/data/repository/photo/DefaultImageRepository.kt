package net.pengcook.android.data.repository.photo

import net.pengcook.android.data.datasource.photo.ImageRemoteDataSource
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultImageRepository
    @Inject
    constructor(
        private val imageRemoteDataSource: ImageRemoteDataSource,
    ) : ImageRepository {
        override suspend fun uploadImage(
            url: String,
            file: File,
        ): Result<Boolean> {
            return runCatching {
                val response = imageRemoteDataSource.uploadImage(url, file)
                when (response.code()) {
                    CODE_SUCCESSFUL -> true
                    else -> false
                }
            }
        }

        override suspend fun fetchImageUri(keyName: String): Result<String> {
            return runCatching {
                val response = imageRemoteDataSource.fetchImageUri(keyName)
                response.body()?.url ?: throw RuntimeException("Failed to fetch image uri")
            }
        }

        companion object {
            private const val CODE_SUCCESSFUL = 200
        }
    }
