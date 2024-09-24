package net.pengcook.android.presentation.comment

import dagger.assisted.AssistedFactory

@AssistedFactory
interface CommentViewModelFactory {
    fun create(recipeId: Long): CommentViewModel
}
