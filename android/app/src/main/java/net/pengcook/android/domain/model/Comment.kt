package net.pengcook.android.domain.model

data class Comment(
    val id: Long,
    val user: User,
    val comment: String,
    val date: Long,
)
