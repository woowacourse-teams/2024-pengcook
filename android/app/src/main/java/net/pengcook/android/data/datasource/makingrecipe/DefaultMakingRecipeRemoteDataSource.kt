package net.pengcook.android.data.datasource.makingrecipe

import net.pengcook.android.data.model.makingrecipe.RecipeCreationResponse
import net.pengcook.android.data.model.makingrecipe.request.RecipeCreationRequest
import net.pengcook.android.data.remote.api.MakingRecipeService
import net.pengcook.android.data.util.network.NetworkResponseHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMakingRecipeRemoteDataSource
    @Inject
    constructor(
        private val makingRecipeService: MakingRecipeService,
    ) : MakingRecipeRemoteDataSource, NetworkResponseHandler() {
        override suspend fun fetchImageUri(keyName: String): String {
            val response = makingRecipeService.fetchImageUri(keyName)
            if (response.isSuccessful) {
                return response.body()?.url ?: throw Exception("이미지 URI를 받을 수 없습니다.")
            } else {
                throw Exception("이미지 URI 요청 실패")
            }
        }

        override suspend fun uploadImageToS3(
            presignedUrl: String,
            file: File,
        ) {
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val response = makingRecipeService.uploadImageToS3(presignedUrl, requestFile)
            if (!response.isSuccessful) {
                throw Exception("이미지 업로드 실패")
            }
        }

        override suspend fun uploadNewRecipe(
            accessToken: String,
            newRecipe: RecipeCreationRequest,
        ): Response<RecipeCreationResponse> {
            return makingRecipeService.postNewRecipe(accessToken, newRecipe)
        }
    }
