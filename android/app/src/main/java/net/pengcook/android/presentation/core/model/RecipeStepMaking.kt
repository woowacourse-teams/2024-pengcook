package net.pengcook.android.presentation.core.model

data class RecipeStepMaking(
    val stepId: Long,
    val recipeId: Long,
    val description: String,
    val image: String,
    val sequence: Int,
    val imageUri: String,
    val cookingTime: String,
    val imageUploaded: Boolean,
) {
    init {
        require(cookingTime.matches(Regex("\\d{2}:\\d{2}:\\d{2}"))) {
            "cookingTime must be in the format of HH:MM:SS"
        }
    }
}
