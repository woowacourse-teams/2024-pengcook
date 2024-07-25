package net.pengcook.android.presentation.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val ingredientId: Long,
    val ingredientName: String,
    val requirement: String,
) : Parcelable
