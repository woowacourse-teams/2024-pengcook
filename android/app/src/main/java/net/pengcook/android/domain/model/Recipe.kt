package net.pengcook.android.domain.model

data class Recipe(
    val title: String,
    val category: String,
    val thumbnail: String,
    val user: User,
    val favoriteCount: Long,
    val ingredients: List<String>,
    val timeRequired: Int,
    val difficulty: String,
    val introduction: String,
)
