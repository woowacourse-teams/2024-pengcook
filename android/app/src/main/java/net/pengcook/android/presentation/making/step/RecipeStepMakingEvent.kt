package net.pengcook.android.presentation.making.step

sealed interface RecipeStepMakingEvent {
    data object AddImage : RecipeStepMakingEvent

    data object PostImageSuccessful : RecipeStepMakingEvent

    data object PostImageFailure : RecipeStepMakingEvent

    data class PresignedUrlRequestSuccessful(val presignedUrl: String) : RecipeStepMakingEvent

    data object NavigateBackToDescription : RecipeStepMakingEvent

    data object FormNotCompleted : RecipeStepMakingEvent

    data object ImageNotUploaded : RecipeStepMakingEvent

    data object RecipePostSuccessful : RecipeStepMakingEvent

    data object RecipePostFailure : RecipeStepMakingEvent
}
