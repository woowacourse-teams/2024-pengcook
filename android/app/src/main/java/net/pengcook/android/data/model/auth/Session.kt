package net.pengcook.android.data.model.auth

data class Session(
    val platformToken: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val fcmToken: String?,
    val currentPlatform: String?,
)
