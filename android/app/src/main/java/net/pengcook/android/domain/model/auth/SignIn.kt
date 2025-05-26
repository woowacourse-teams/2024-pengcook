package net.pengcook.android.domain.model.auth

data class SignIn(
    val accessToken: String?,
    val refreshToken: String?,
    val registered: Boolean,
)
