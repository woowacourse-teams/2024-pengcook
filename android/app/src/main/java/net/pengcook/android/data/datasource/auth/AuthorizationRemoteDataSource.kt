package net.pengcook.android.data.datasource.auth

import net.pengcook.android.data.model.auth.request.IdTokenRequest
import net.pengcook.android.data.model.auth.request.RefreshTokenRequest
import net.pengcook.android.data.model.auth.request.SignUpRequest
import net.pengcook.android.data.model.auth.response.RefreshedTokensResponse
import net.pengcook.android.data.model.auth.response.SignInResponse
import net.pengcook.android.data.model.auth.response.SignUpResponse
import net.pengcook.android.data.model.auth.response.UserInformationResponse
import net.pengcook.android.data.model.auth.response.UsernameDuplicationResponse
import retrofit2.Response

interface AuthorizationRemoteDataSource {
    suspend fun signIn(
        platformName: String,
        idToken: IdTokenRequest,
    ): Response<SignInResponse>

    suspend fun signUp(
        platformName: String,
        signUpData: SignUpRequest,
    ): Response<SignUpResponse>

    suspend fun checkUsernameDuplication(username: String): Response<UsernameDuplicationResponse>

    suspend fun fetchAccessToken(refreshToken: RefreshTokenRequest): Response<RefreshedTokensResponse>

    suspend fun fetchUserInformation(accessToken: String): Response<UserInformationResponse>

    suspend fun checkSignInStatus(accessToken: String): Response<Unit>

    suspend fun deleteAccount(accessToken: String): Response<Unit>
}
