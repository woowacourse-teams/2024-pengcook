package net.pengcook.android.presentation.edit

sealed interface EditRecipeEvent {
    data class ChangeImage(
        val id: Int,
    ) : EditRecipeEvent

    data class ImageDeletionSuccessful(
        val id: Int,
    ) : EditRecipeEvent

    data object UnexpectedError : EditRecipeEvent

    data object NullPhotoPath : EditRecipeEvent

    data object PostImageSuccessful : EditRecipeEvent

    data object PostImageFailure : EditRecipeEvent

    data object RecipeSavingSuccessful : EditRecipeEvent

    data object RecipeSavingFailure : EditRecipeEvent

    data object EditCancellation : EditRecipeEvent

    data object AddThumbnailImage : EditRecipeEvent

    data object AddStepImages : EditRecipeEvent

    data object StepImageSelectionFailure : EditRecipeEvent

    data object DescriptionFormNotCompleted : EditRecipeEvent

    data object RecipePostFailure : EditRecipeEvent

    data object RecipePostSuccessful : EditRecipeEvent

    data class NavigateToEditStep(
        val sequence: Int,
    ) : EditRecipeEvent
}
