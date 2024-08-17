package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.PresignedUrlResponse
import net.pengcook.android.data.model.makingrecipe.RecipeCreationResponse
import net.pengcook.android.data.model.makingrecipe.request.RecipeCreationRequest
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url

interface MakingRecipeService {
    @GET("/image")
    suspend fun fetchImageUri(
        @Query("fileName") keyName: String,
    ): Response<PresignedUrlResponse>

    @PUT
    suspend fun uploadImageToS3(
        @Url url: String,
        @Body image: RequestBody,
    ): Response<Unit>

    @POST("/recipes")
    suspend fun postNewRecipe(
        @Header("Authorization") accessToken: String,
        @Body newRecipe: RecipeCreationRequest,
    ): Response<RecipeCreationResponse>
}
