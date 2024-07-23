package net.pengcook.android.domain.model

data class Comment(
    val id: Long,
    val userId: Long,
    val userName: String,
    val profile: String,
    val comment: String,
    val date: Long,
)
