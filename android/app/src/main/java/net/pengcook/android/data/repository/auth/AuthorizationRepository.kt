package net.pengcook.android.data.repository.auth

import net.pengcook.android.domain.model.auth.RefreshedTokens
import net.pengcook.android.domain.model.auth.SignIn
import net.pengcook.android.domain.model.auth.SignUp
import net.pengcook.android.domain.model.auth.UserInformation
import net.pengcook.android.domain.model.auth.UserSignUpForm

interface AuthorizationRepository {
    suspend fun signIn(
        platformName: String,
        idToken: String,
    ): Result<SignIn>

    suspend fun signUp(
        platformName: String,
        userSignUpForm: UserSignUpForm,
    ): Result<SignUp>

    suspend fun checkUsernameDuplication(username: String): Result<Boolean>

    suspend fun fetchAccessToken(refreshToken: String): Result<RefreshedTokens>

    suspend fun fetchUserInformation(accessToken: String): Result<UserInformation>

    suspend fun deleteAccount(): Result<Unit>
}
