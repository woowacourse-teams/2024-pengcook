package net.pengcook.android.presentation.making.step

sealed interface RecipeStepMakingEvent {
    data object AddImage : RecipeStepMakingEvent

    data class PresignedUrlRequestSuccessful(val presignedUrl: String) : RecipeStepMakingEvent

    data object PostImageSuccessful : RecipeStepMakingEvent

    data object PostImageFailure : RecipeStepMakingEvent

    data object DescriptionFormNotCompleted : RecipeStepMakingEvent

    data object PostStepFailure : RecipeStepMakingEvent
}
