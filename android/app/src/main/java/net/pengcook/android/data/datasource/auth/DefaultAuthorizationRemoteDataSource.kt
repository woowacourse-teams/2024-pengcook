package net.pengcook.android.data.datasource.auth

import net.pengcook.android.data.model.auth.request.IdTokenRequest
import net.pengcook.android.data.model.auth.request.RefreshTokenRequest
import net.pengcook.android.data.model.auth.request.SignUpRequest
import net.pengcook.android.data.model.auth.response.RenewedTokensResponse
import net.pengcook.android.data.model.auth.response.SignInResponse
import net.pengcook.android.data.model.auth.response.SignUpResponse
import net.pengcook.android.data.model.auth.response.UserInformationResponse
import net.pengcook.android.data.model.auth.response.UsernameDuplicationResponse
import net.pengcook.android.data.remote.api.AuthorizationService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthorizationRemoteDataSource
    @Inject
    constructor(
        private val authorizationService: AuthorizationService,
    ) : AuthorizationRemoteDataSource {
        override suspend fun signIn(
            platformName: String,
            idToken: IdTokenRequest,
        ): Response<SignInResponse> {
            return authorizationService.signIn(platformName, idToken)
        }

        override suspend fun signUp(
            platformName: String,
            signUpData: SignUpRequest,
        ): Response<SignUpResponse> {
            return authorizationService.signUp(platformName, signUpData)
        }

        override suspend fun checkUsernameDuplication(username: String): Response<UsernameDuplicationResponse> {
            return authorizationService.checkUsernameDuplication(username)
        }

        override suspend fun fetchAccessToken(refreshToken: RefreshTokenRequest): Response<RenewedTokensResponse> {
            return authorizationService.fetchAccessToken(refreshToken)
        }

        override suspend fun fetchUserInformation(accessToken: String): Response<UserInformationResponse> {
            return authorizationService.fetchUserInformation(accessToken)
        }

        override suspend fun checkSignInStatus(accessToken: String): Response<Unit> {
            return authorizationService.checkSignInStatus(accessToken)
        }

        override suspend fun deleteAccount(accessToken: String): Response<Unit> {
            return authorizationService.deleteAccount(accessToken)
        }
    }
