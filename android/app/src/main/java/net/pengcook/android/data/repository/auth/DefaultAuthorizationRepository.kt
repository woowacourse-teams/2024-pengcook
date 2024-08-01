package net.pengcook.android.data.repository.auth

import net.pengcook.android.data.datasource.auth.AuthorizationRemoteDataSource
import net.pengcook.android.data.model.auth.request.IdTokenRequest
import net.pengcook.android.data.model.auth.request.RefreshTokenRequest
import net.pengcook.android.data.util.mapper.toRefreshedTokens
import net.pengcook.android.data.util.mapper.toSignIn
import net.pengcook.android.data.util.mapper.toSignUp
import net.pengcook.android.data.util.mapper.toSignUpRequest
import net.pengcook.android.data.util.mapper.toUserInformation
import net.pengcook.android.data.util.mapper.toUsernameAvailable
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.domain.model.auth.RefreshedTokens
import net.pengcook.android.domain.model.auth.SignIn
import net.pengcook.android.domain.model.auth.SignUp
import net.pengcook.android.domain.model.auth.UserInformation
import net.pengcook.android.domain.model.auth.UserSignUpForm

class DefaultAuthorizationRepository(
    private val authorizationRemoteDataSource: AuthorizationRemoteDataSource,
) : AuthorizationRepository, NetworkResponseHandler() {
    override suspend fun signIn(
        platformName: String,
        idToken: String,
    ): Result<SignIn> {
        return runCatching {
            val response =
                authorizationRemoteDataSource.signIn(platformName, IdTokenRequest(idToken))
            body(response, RESPONSE_CODE_SUCCESS).toSignIn()
        }
    }

    override suspend fun signUp(
        platformName: String,
        userSignUpForm: UserSignUpForm,
    ): Result<SignUp> {
        return runCatching {
            val response =
                authorizationRemoteDataSource.signUp(
                    platformName = platformName,
                    signUpData = userSignUpForm.toSignUpRequest(),
                )
            body(response, RESPONSE_CODE_SIGN_IN_SUCCESS).toSignUp()
        }
    }

    override suspend fun fetchUsernameDuplication(username: String): Result<Boolean> {
        return runCatching {
            val response = authorizationRemoteDataSource.fetchUsernameDuplication(username)
            body(response, RESPONSE_CODE_SUCCESS).toUsernameAvailable()
        }
    }

    override suspend fun fetchAccessToken(refreshToken: String): Result<RefreshedTokens> {
        return runCatching {
            val response =
                authorizationRemoteDataSource.fetchAccessToken(RefreshTokenRequest(refreshToken))
            body(response, RESPONSE_CODE_SUCCESS).toRefreshedTokens()
        }
    }

    override suspend fun fetchUserInformation(accessToken: String): Result<UserInformation> {
        return runCatching {
            val response = authorizationRemoteDataSource.fetchUserInformation(accessToken)
            body(response, RESPONSE_CODE_SUCCESS).toUserInformation()
        }
    }

    companion object {
        private const val RESPONSE_CODE_SUCCESS = 200
        private const val RESPONSE_CODE_SIGN_IN_SUCCESS = 201
    }
}
