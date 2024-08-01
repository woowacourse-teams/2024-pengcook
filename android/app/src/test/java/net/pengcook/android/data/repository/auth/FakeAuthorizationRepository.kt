package net.pengcook.android.data.repository.auth

import net.pengcook.android.data.datasource.auth.FakeTokenLocalDataSource
import net.pengcook.android.domain.model.auth.RefreshedTokens
import net.pengcook.android.domain.model.auth.SignIn
import net.pengcook.android.domain.model.auth.SignUp
import net.pengcook.android.domain.model.auth.UserInformation
import net.pengcook.android.domain.model.auth.UserSignUpForm

class FakeAuthorizationRepository(
    private val registered: Boolean,
    private val usernames: List<String>,
    private val fakeTokenLocalDataSource: FakeTokenLocalDataSource,
) : AuthorizationRepository {
    private var userInformation: UserInformation? = null
    private var refreshTrial = 0
    private val validPlatform = "google"
    private val validToken = "googleToken"

    override suspend fun signIn(
        platformName: String,
        idToken: String,
    ): Result<SignIn> {
        return runCatching {
            if (platformName != validPlatform || idToken != validToken) throw IllegalArgumentException()
            val accessToken = if (registered) "accessToken" else null
            val refreshToken = if (registered) "refreshToken" else null
            fakeTokenLocalDataSource.apply {
                updateAccessToken(accessToken)
                updateRefreshToken(refreshToken)
            }
            SignIn(accessToken, refreshToken, registered)
        }
    }

    override suspend fun signUp(
        platformName: String,
        userSignUpForm: UserSignUpForm,
    ): Result<SignUp> {
        return runCatching {
            val accessToken = "accessToken"
            val refreshToken = "refreshToken"

            userInformation =
                UserInformation(
                    region = userSignUpForm.country,
                    email = "kmkim2689@gmail.com",
                    id = 1L,
                    image = "image",
                    nickname = userSignUpForm.nickname,
                    username = userSignUpForm.username,
                )

            SignUp(
                accessToken = accessToken,
                refreshToken = refreshToken,
                country = userSignUpForm.country,
                email = "kmkim2689@gmail.com",
                id = 1L,
                image = "image",
                nickname = userSignUpForm.nickname,
                username = userSignUpForm.username,
            )
        }
    }

    override suspend fun fetchUsernameDuplication(username: String): Result<Boolean> {
        return runCatching { username !in usernames }
    }

    override suspend fun fetchAccessToken(refreshToken: String): Result<RefreshedTokens> {
        return runCatching {
            refreshTrial++
            RefreshedTokens(
                accessToken = "accessToken$refreshTrial",
                refreshToken = "refreshToken$refreshTrial",
            )
        }
    }

    override suspend fun fetchUserInformation(accessToken: String): Result<UserInformation> {
        return runCatching { userInformation ?: throw IllegalStateException() }
    }
}
