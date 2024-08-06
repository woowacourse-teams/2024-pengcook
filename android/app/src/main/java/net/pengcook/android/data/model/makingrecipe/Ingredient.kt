package net.pengcook.android.data.model.makingrecipe

data class Ingredient(
    val name: String,
    val requirement: String = "REQUIRED",
    val substitutions: List<String>? = null,
)
