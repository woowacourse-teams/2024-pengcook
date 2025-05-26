package net.pengcook.android.presentation.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Long,
    val username: String,
    val profile: String,
) : Parcelable
