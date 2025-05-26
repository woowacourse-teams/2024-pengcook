package net.pengcook.android.presentation.edit

import dagger.assisted.AssistedFactory

@AssistedFactory
interface EditRecipeViewModelFactory {
    fun create(recipeId: Long): EditRecipeViewModel
}
