package net.pengcook.android.presentation.step

import dagger.assisted.AssistedFactory

@AssistedFactory
interface RecipeStepViewModelFactory {
    fun create(recipeId: Long): RecipeStepViewModel
}
