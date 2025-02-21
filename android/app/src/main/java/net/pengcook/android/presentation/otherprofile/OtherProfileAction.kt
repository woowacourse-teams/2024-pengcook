package net.pengcook.android.presentation.otherprofile

sealed interface OtherProfileAction {
    data object OnBackClick : OtherProfileAction

    data object OnFollowClick : OtherProfileAction

    data object OnUnfollowClick : OtherProfileAction

    data object OnBlockClick : OtherProfileAction

    // TODO : implement Recipe navigation action
    data class OnRecipeClick(val recipeId: Long) : OtherProfileAction
}
