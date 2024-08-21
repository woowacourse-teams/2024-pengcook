package net.pengcook.android.data.model.auth.response

data class RenewedTokensResponse(
    val accessToken: String,
    val refreshToken: String,
)
