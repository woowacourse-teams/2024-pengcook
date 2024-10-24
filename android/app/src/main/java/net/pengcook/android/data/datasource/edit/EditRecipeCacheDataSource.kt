package net.pengcook.android.data.datasource.edit

object EditRecipeCacheDataSource {
    private var savedRecipeDescription: SavedRecipeDescription? = null

    var savedRecipeSteps: List<SavedRecipeSteps> = listOf()
        private set

    fun fetchSavedRecipeDescription(): SavedRecipeDescription {
        val data = savedRecipeDescription
        check(data != null) { "Saved recipe description is not available" }
        return data
    }

    fun saveRecipeDescription(changedRecipeDescription: SavedRecipeDescription) {
        this.savedRecipeDescription = changedRecipeDescription
    }

    fun clearSavedRecipeDescription() {
        savedRecipeDescription = null
    }

    fun saveRecipeSteps(savedRecipeSteps: List<SavedRecipeSteps>) {
        this.savedRecipeSteps = savedRecipeSteps
    }

    fun clearSavedRecipeSteps() {
        savedRecipeSteps = emptyList()
    }
}

data class SavedRecipeDescription(
    val id: Long,
    val title: String,
    val imageUri: String,
    val description: String,
    val cookingTime: String,
    val categories: List<String>,
    val ingredients: List<SavedIngredient>,
    val difficulty: Int,
    val thumbnail: String,
)

data class SavedIngredient(
    val name: String,
    val requirement: String = "REQUIRED",
    val substitutions: List<String> = emptyList(),
)

data class SavedRecipeSteps(
    val sequence: Int,
    val imageUri: String?,
    val imageTitle: String?,
    val cookingTime: String?,
    val description: String?,
    val imageUploaded: Boolean,
)
