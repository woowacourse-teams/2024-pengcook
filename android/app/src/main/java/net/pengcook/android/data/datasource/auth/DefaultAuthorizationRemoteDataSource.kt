package net.pengcook.android.data.datasource.auth

import net.pengcook.android.data.model.auth.request.IdTokenRequest
import net.pengcook.android.data.model.auth.request.RefreshTokenRequest
import net.pengcook.android.data.model.auth.response.SignInResponse
import net.pengcook.android.data.model.auth.request.SignUpRequest
import net.pengcook.android.data.model.auth.response.SignUpResponse
import net.pengcook.android.data.model.auth.response.RefreshedTokensResponse
import net.pengcook.android.data.model.auth.response.UserInformationResponse
import net.pengcook.android.data.model.auth.response.UsernameDuplicationResponse
import net.pengcook.android.data.remote.api.AuthorizationService
import retrofit2.Response

class DefaultAuthorizationRemoteDataSource(
    private val authorizationService: AuthorizationService,
) : AuthorizationRemoteDataSource {
    override suspend fun signIn(platformName: String, idToken: IdTokenRequest): Response<SignInResponse> {
        return authorizationService.signIn(platformName, idToken)
    }

    override suspend fun signUp(platformName: String, signUpData: SignUpRequest): Response<SignUpResponse> {
        return authorizationService.signUp(platformName, signUpData)
    }

    override suspend fun fetchUsernameDuplication(username: String): Response<UsernameDuplicationResponse> {
        return authorizationService.fetchUsernameDuplication(username)
    }

    override suspend fun fetchAccessToken(refreshToken: RefreshTokenRequest): Response<RefreshedTokensResponse> {
        return authorizationService.fetchAccessToken(refreshToken)
    }

    override suspend fun fetchUserInformation(accessToken: String): Response<UserInformationResponse> {
        return authorizationService.fetchUserInformation(accessToken)
    }
}
