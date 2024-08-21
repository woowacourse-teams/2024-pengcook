package net.pengcook.android.data.model.makingrecipe.request

data class IngredientRequest(
    val name: String,
    val requirement: String = "REQUIRED",
    val substitutions: List<String>? = null,
)
