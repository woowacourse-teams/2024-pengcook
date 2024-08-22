package net.pengcook.android.presentation.detail

sealed class DetailRecipeUiEvent {
    data object NavigateToStep : DetailRecipeUiEvent()

    data object NavigateToComment : DetailRecipeUiEvent()

    data object NavigateBack : DetailRecipeUiEvent()

    data class NavigateBackWithDelete(
        val action: String,
    ) : DetailRecipeUiEvent()

    data class NavigateBackWithBlock(
        val username: String,
    ) : DetailRecipeUiEvent()
}
