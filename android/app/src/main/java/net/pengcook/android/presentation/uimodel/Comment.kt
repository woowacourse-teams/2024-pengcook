package net.pengcook.android.presentation.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val id: Long,
    val user: User,
    val comment: String,
    val date: Long,
) : Parcelable
