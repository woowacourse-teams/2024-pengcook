package net.pengcook.android.data.repository.image

import net.pengcook.android.data.repository.photo.ImageRepository
import java.io.File

class FakeImageRepository : ImageRepository {
    override suspend fun uploadImage(
        url: String,
        file: File,
    ): Result<Boolean> {
        return runCatching { true }
    }

    override suspend fun fetchImageUri(keyName: String): Result<String> {
        return runCatching { "https://example.com/image.jpg" }
    }
}
