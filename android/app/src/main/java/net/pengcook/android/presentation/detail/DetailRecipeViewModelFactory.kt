package net.pengcook.android.presentation.detail

import dagger.assisted.AssistedFactory

@AssistedFactory
interface DetailRecipeViewModelFactory {
    fun create(recipeId: Long): DetailRecipeViewModel
}
