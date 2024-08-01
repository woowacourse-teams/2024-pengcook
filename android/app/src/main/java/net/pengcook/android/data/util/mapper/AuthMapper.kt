package net.pengcook.android.data.util.mapper

import net.pengcook.android.data.model.auth.request.SignUpRequest
import net.pengcook.android.data.model.auth.response.RefreshedTokensResponse
import net.pengcook.android.data.model.auth.response.SignInResponse
import net.pengcook.android.data.model.auth.response.SignUpResponse
import net.pengcook.android.data.model.auth.response.UserInformationResponse
import net.pengcook.android.data.model.auth.response.UsernameDuplicationResponse
import net.pengcook.android.domain.model.auth.RefreshedTokens
import net.pengcook.android.domain.model.auth.SignIn
import net.pengcook.android.domain.model.auth.SignUp
import net.pengcook.android.domain.model.auth.UserInformation
import net.pengcook.android.domain.model.auth.UserSignUpForm

fun SignInResponse.toSignIn(): SignIn =
    SignIn(
        accessToken = accessToken,
        refreshToken = refreshToken,
        registered = registered,
    )

fun SignUpResponse.toSignUp(): SignUp =
    SignUp(
        accessToken = accessToken,
        country = country,
        email = email,
        id = id,
        image = image,
        nickname = nickname,
        refreshToken = refreshToken,
        username = username,
    )

fun UserSignUpForm.toSignUpRequest(): SignUpRequest =
    SignUpRequest(
        country = country,
        idToken = idToken,
        nickname = nickname,
        username = username,
    )

fun UsernameDuplicationResponse.toUsernameAvailable(): Boolean = available

fun RefreshedTokensResponse.toRefreshedTokens(): RefreshedTokens =
    RefreshedTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )

fun UserInformationResponse.toUserInformation(): UserInformation =
    UserInformation(
        email = email,
        id = id,
        image = image,
        nickname = nickname,
        region = region,
        username = username,
    )
