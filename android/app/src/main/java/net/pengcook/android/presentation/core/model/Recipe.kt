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

data class RecipeForList(
    val user: User,
    val commentCount: Long,
    val createdAt: String,
    val likeCount: Long,
    val mine: Boolean,
    val recipeId: Long,
    val thumbnail: String,
    val title: String,
)

@Parcelize
data class RecipeForItem(
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
    val isLiked: Boolean,
) : Parcelable
