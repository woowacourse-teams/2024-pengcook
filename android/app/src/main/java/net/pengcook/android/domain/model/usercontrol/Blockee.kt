package net.pengcook.android.domain.model.usercontrol

data class Blockee(
    val id: Long,
    val email: String,
    val username: String,
    val nickname: String,
    val image: String,
    val region: String,
)
