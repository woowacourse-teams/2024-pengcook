package net.pengcook.android.data.model.auth.response

data class RefreshedTokensResponse(
    val accessToken: String,
    val refreshToken: String,
)
