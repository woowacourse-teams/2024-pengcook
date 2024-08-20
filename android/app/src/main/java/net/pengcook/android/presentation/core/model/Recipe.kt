package net.pengcook.android.presentation.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val recipeId: Long = 1L,
    val title: String,
    val category: List<String>,
    val cookingTime: String,
    val thumbnail: String,
    val user: User,
    val likeCount: Long,
    val ingredients: List<Ingredient>,
    val difficulty: Int,
    val introduction: String,
    val commentCount: Long,
    val mine: Boolean,
) : Parcelable
