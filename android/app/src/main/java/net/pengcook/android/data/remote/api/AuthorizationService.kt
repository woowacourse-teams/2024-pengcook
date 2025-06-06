package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.auth.request.IdTokenRequest
import net.pengcook.android.data.model.auth.request.RefreshTokenRequest
import net.pengcook.android.data.model.auth.request.SignUpRequest
import net.pengcook.android.data.model.auth.response.RenewedTokensResponse
import net.pengcook.android.data.model.auth.response.SignInResponse
import net.pengcook.android.data.model.auth.response.SignUpResponse
import net.pengcook.android.data.model.auth.response.UserInformationResponse
import net.pengcook.android.data.model.auth.response.UsernameDuplicationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthorizationService {
    @POST("/token/refresh")
    suspend fun fetchAccessToken(
        @Body refreshToken: RefreshTokenRequest,
    ): Response<RenewedTokensResponse>

    @GET("/user/me")
    suspend fun fetchUserInformation(
        @Header("Authorization") accessToken: String,
    ): Response<UserInformationResponse>

    @POST("/oauth/{platform}/login")
    suspend fun signIn(
        @Path("platform") platform: String,
        @Body idToken: IdTokenRequest,
    ): Response<SignInResponse>

    @POST("/oauth/{platform}/sign-up")
    suspend fun signUp(
        @Path("platform") platform: String,
        @Body signUpData: SignUpRequest,
    ): Response<SignUpResponse>

    @GET("/user/username/check")
    suspend fun checkUsernameDuplication(
        @Query("username") username: String,
    ): Response<UsernameDuplicationResponse>

    @GET("/token/check")
    suspend fun checkSignInStatus(
        @Header("Authorization") accessToken: String,
    ): Response<Unit>

    @DELETE("/user/me")
    suspend fun deleteAccount(
        @Header("Authorization") accessToken: String,
    ): Response<Unit>
}
