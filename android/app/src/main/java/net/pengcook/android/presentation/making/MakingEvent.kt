package net.pengcook.android.presentation.making

sealed interface MakingEvent {
    data class NavigateToMakingStep(val recipeId: Long) : MakingEvent

    data object AddImage : MakingEvent

    data class PresignedUrlRequestSuccessful(val presignedUrl: String) : MakingEvent

    data object PostImageSuccessful : MakingEvent

    data object PostImageFailure : MakingEvent

    data object DescriptionFormNotCompleted : MakingEvent

    data object PostRecipeFailure : MakingEvent
}
