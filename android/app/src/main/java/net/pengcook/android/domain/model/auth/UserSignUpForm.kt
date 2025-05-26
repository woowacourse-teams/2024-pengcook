package net.pengcook.android.domain.model.auth

data class UserSignUpForm(
    val idToken: String,
    val country: String,
    val nickname: String,
    val username: String,
    val image: String?,
)
