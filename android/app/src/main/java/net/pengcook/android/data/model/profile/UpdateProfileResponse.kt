package net.pengcook.android.data.model.profile

data class UpdateProfileResponse(
    val userId: Long,
    val email: String,
    val username: String,
    val nickname: String,
    val image: String,
    val region: String,
    val introduction: String,
)
