package net.pengcook.android.data.repository.making

import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import java.io.File

class FakeMakingRecipeRepository : MakingRecipeRepository {
    private var id: Long = 0

    override suspend fun fetchImageUri(keyName: String): String {
        return "uri"
    }

    override suspend fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    ) {
    }

    override suspend fun postRecipeDescription(recipeDescription: RecipeDescription): Result<Long> {
        return runCatching {
            id++
        }
    }
}
