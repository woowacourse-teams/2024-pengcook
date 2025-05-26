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

    val minute: String = if (cookingTime.split(":")[1] == "00") "" else cookingTime.split(":")[1]
    val second: String = if (cookingTime.split(":")[2] == "00") "" else cookingTime.split(":")[2]

    companion object {
        val EMPTY =
            RecipeStepMaking(
                stepId = 0,
                recipeId = 0,
                description = "",
                image = "",
                sequence = 0,
                imageUri = "",
                cookingTime = "00:00:00",
                imageUploaded = false,
            )
    }
}
