package net.pengcook.android.presentation.making

sealed interface RecipeMakingEvent {
    data class NavigateToMakingStep(val recipeId: Long) : RecipeMakingEvent

    data object AddImage : RecipeMakingEvent

    data class PresignedUrlRequestSuccessful(val presignedUrl: String) : RecipeMakingEvent

    data object PostImageSuccessful : RecipeMakingEvent

    data object PostImageFailure : RecipeMakingEvent

    data object DescriptionFormNotCompleted : RecipeMakingEvent

    data object PostRecipeFailure : RecipeMakingEvent

    data object MakingCancellation : RecipeMakingEvent
}
