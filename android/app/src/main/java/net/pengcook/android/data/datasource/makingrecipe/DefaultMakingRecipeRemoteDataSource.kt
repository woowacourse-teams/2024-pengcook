package net.pengcook.android.data.datasource.makingrecipe

import net.pengcook.android.data.remote.api.MakingRecipeService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DefaultMakingRecipeRemoteDataSource(
    private val makingRecipeService: MakingRecipeService
) : MakingRecipeRemoteDataSource {

    override suspend fun fetchImageUri(keyName: String): String {
        val response = makingRecipeService.fetchImageUri(keyName)
        if (response.isSuccessful) {
            return response.body()?.url ?: throw Exception("이미지 URI를 받을 수 없습니다.")
        } else {
            throw Exception("이미지 URI 요청 실패")
        }
    }

    override suspend fun uploadImageToS3(presignedUrl: String, file: File) {
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val response = makingRecipeService.uploadImageToS3(presignedUrl, requestFile)
        if (!response.isSuccessful) {
            throw Exception("이미지 업로드 실패")
        }
    }
}