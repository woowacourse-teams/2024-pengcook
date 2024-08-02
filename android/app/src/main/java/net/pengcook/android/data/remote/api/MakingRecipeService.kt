package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.PresignedUrlResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url

interface MakingRecipeService {
    @GET("/image")
    suspend fun fetchImageUri(
        @Query("keyName") keyName: String,
    ): Response<PresignedUrlResponse>

    @PUT
    suspend fun uploadImageToS3(
        @Url url: String,
        @Body image: RequestBody,
    ): Response<Unit>
}
