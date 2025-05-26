package net.pengcook.android.data.model.auth.response

data class SignInResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val registered: Boolean,
)
