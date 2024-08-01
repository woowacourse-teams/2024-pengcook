package net.pengcook.android.data.model.auth.response

data class UserInformationResponse(
    val email: String,
    val id: Long,
    val image: String,
    val nickname: String,
    val region: String,
    val username: String,
)
