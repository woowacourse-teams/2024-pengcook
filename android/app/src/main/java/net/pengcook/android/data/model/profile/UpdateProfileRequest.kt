package net.pengcook.android.data.model.profile

data class UpdateProfileRequest(
    val username: String,
    val nickname: String,
    val image: String?,
    val region: String,
    val introduction: String,
)
