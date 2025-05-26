package net.pengcook.android.presentation.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val commentId: Long,
    val userId: Long,
    val userImage: String,
    val userName: String,
    val createdAt: String,
    val message: String,
    val mine: Boolean,
) : Parcelable
