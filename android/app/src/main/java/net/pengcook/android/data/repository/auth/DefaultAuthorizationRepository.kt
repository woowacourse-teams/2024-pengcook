package net.pengcook.android.data.repository.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import net.pengcook.android.data.datasource.auth.AuthorizationRemoteDataSource
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.model.auth.Session
import net.pengcook.android.data.model.auth.request.IdTokenRequest
import net.pengcook.android.data.model.auth.request.RefreshTokenRequest
import net.pengcook.android.data.util.mapper.toRenewedTokens
import net.pengcook.android.data.util.mapper.toSignIn
import net.pengcook.android.data.util.mapper.toSignUp
import net.pengcook.android.data.util.mapper.toSignUpRequest
import net.pengcook.android.data.util.mapper.toUserInformation
import net.pengcook.android.data.util.mapper.toUsernameAvailable
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.domain.model.auth.RenewedTokens
import net.pengcook.android.domain.model.auth.SignIn
import net.pengcook.android.domain.model.auth.SignInResult
import net.pengcook.android.domain.model.auth.SignUp
import net.pengcook.android.domain.model.auth.UserInformation
import net.pengcook.android.domain.model.auth.UserSignUpForm
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthorizationRepository @Inject constructor(
    private val authorizationRemoteDataSource: AuthorizationRemoteDataSource,
    private val sessionLocalDataSource: SessionLocalDataSource,
) : AuthorizationRepository, NetworkResponseHandler() {
    private val sessionData: Flow<Session?>
        get() = sessionLocalDataSource.sessionData

    private suspend fun accessToken(): String =
        sessionData.firstOrNull()?.accessToken ?: throw RuntimeException()

    private suspend fun refreshToken(): String =
        sessionData.firstOrNull()?.refreshToken ?: throw RuntimeException()

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

    override suspend fun checkUsernameDuplication(username: String): Result<Boolean> {
        return runCatching {
            val response = authorizationRemoteDataSource.checkUsernameDuplication(username)
            body(response, RESPONSE_CODE_SUCCESS).toUsernameAvailable()
        }
    }

    override suspend fun fetchRenewedTokens(): Result<RenewedTokens> {
        return runCatching {
            val response =
                authorizationRemoteDataSource.fetchAccessToken(RefreshTokenRequest(refreshToken()))
            body(response, RESPONSE_CODE_SUCCESS).toRenewedTokens()
        }
    }

    override suspend fun fetchUserInformation(): Result<UserInformation> {
        return runCatching {
            val response = authorizationRemoteDataSource.fetchUserInformation(accessToken())
            body(response, RESPONSE_CODE_SUCCESS).toUserInformation()
        }
    }

    override suspend fun checkSignInStatus(): Result<SignInResult> {
        return runCatching {
            val response = authorizationRemoteDataSource.checkSignInStatus(accessToken())
            when (response.code()) {
                RESPONSE_CODE_SUCCESS -> SignInResult.SUCCESSFUL
                RESPONSE_CODE_TOKEN_EXPIRED -> SignInResult.ACCESS_TOKEN_EXPIRED
                RESPONSE_CODE_USER_NOT_FOUND -> SignInResult.USER_NOT_FOUND
                else -> SignInResult.SERVER_ERROR
            }
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return runCatching {
            val accessToken =
                sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
            val response = authorizationRemoteDataSource.deleteAccount(accessToken)
            body(response, RESPONSE_CODE_DELETE_SUCCESS)
        }
    }

    companion object {
        private const val RESPONSE_CODE_SUCCESS = 200
        private const val RESPONSE_CODE_SIGN_IN_SUCCESS = 201
        private const val RESPONSE_CODE_TOKEN_EXPIRED = 401
        private const val RESPONSE_CODE_USER_NOT_FOUND = 404
        private const val RESPONSE_CODE_DELETE_SUCCESS = 204
    }
}
