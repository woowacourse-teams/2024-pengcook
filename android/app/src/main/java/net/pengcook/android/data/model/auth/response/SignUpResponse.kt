package net.pengcook.android.data.model.auth.response


data class SignUpResponse(
    val accessToken: String,
    val country: String,
    val email: String,
    val id: Long,
    val image: String,
    val nickname: String,
    val refreshToken: String,
    val username: String
)
