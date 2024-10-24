package net.pengcook.android.presentation.detail

import net.pengcook.android.presentation.core.model.RecipeForItem

sealed class DetailRecipeUiEvent {
    data object LoadRecipeFailure : DetailRecipeUiEvent()

    data object NavigateToStep : DetailRecipeUiEvent()

    data object NavigateToComment : DetailRecipeUiEvent()

    data object NavigateBack : DetailRecipeUiEvent()

    data class NavigateBackWithDelete(
        val action: String,
    ) : DetailRecipeUiEvent()

    data class NavigateBackWithBlock(
        val username: String,
    ) : DetailRecipeUiEvent()

    data object DeletionError : DetailRecipeUiEvent()

    data class OpenMenu(val recipe: RecipeForItem) : DetailRecipeUiEvent()
}
