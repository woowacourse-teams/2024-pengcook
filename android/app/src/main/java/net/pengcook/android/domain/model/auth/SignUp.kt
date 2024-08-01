package net.pengcook.android.domain.model.auth

data class SignUp(
    val accessToken: String,
    val country: String,
    val email: String,
    val id: Long,
    val image: String,
    val nickname: String,
    val refreshToken: String,
    val username: String,
)
