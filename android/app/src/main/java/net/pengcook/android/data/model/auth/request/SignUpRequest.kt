package net.pengcook.android.data.model.auth.request

data class SignUpRequest(
    val country: String,
    val idToken: String,
    val nickname: String,
    val username: String,
    val image: String?,
)
