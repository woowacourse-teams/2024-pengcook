package net.pengcook.android.domain.model.auth

data class UserInformation(
    val email: String,
    val id: Long,
    val image: String,
    val nickname: String,
    val region: String,
    val username: String,
)
