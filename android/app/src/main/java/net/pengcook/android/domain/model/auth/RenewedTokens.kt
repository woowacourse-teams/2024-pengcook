package net.pengcook.android.domain.model.auth

data class RenewedTokens(
    val accessToken: String,
    val refreshToken: String,
)
