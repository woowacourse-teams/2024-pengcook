package net.pengcook.android.presentation.making

sealed interface RecipeMakingEvent {
    data class NavigateToMakingStep(val recipeId: Long) : RecipeMakingEvent

    data object AddImage : RecipeMakingEvent

    data object AddStepImages : RecipeMakingEvent

    data class PresignedUrlRequestSuccessful(val presignedUrl: String) : RecipeMakingEvent

    data class StepImagesPresignedUrlRequestSuccessful(val presignedUrls: List<String>) : RecipeMakingEvent

    data object PostImageSuccessful : RecipeMakingEvent

    data object PostImageFailure : RecipeMakingEvent

    data object DescriptionFormNotCompleted : RecipeMakingEvent

    data object PostRecipeFailure : RecipeMakingEvent

    data object MakingCancellation : RecipeMakingEvent

    data object PostStepImageCompleted : RecipeMakingEvent
}

sealed interface RecipeMakingEvent2 {
    data object UnexpectedError : RecipeMakingEvent2

//    data class ThumbnailPresignedUrlSuccessful(val presignedUrl: String) : RecipeMakingEvent2

    data object NullPhotoPath : RecipeMakingEvent2

    data object PostImageSuccessful : RecipeMakingEvent2

    data object PostImageFailure : RecipeMakingEvent2

    data object RecipeSavingSuccessful : RecipeMakingEvent2

    data object RecipeSavingFailure : RecipeMakingEvent2

    data object MakingCancellation : RecipeMakingEvent2

    data object AddThumbnailImage : RecipeMakingEvent2

    data object AddStepImages : RecipeMakingEvent2

    data object StepImageSelectionFailure : RecipeMakingEvent2
}
