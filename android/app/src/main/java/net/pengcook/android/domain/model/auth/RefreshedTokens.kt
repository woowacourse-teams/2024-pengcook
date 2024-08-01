package net.pengcook.android.domain.model.auth

data class RefreshedTokens(
    val accessToken: String,
    val refreshToken: String,
)
