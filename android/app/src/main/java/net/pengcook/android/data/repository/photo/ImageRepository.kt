package net.pengcook.android.data.repository.photo

import java.io.File

interface ImageRepository {
    suspend fun uploadImage(
        url: String,
        file: File,
    ): Result<Boolean>

    suspend fun fetchImageUri(keyName: String): Result<String>
}
