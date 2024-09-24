package net.pengcook.android.presentation.detail

import dagger.assisted.AssistedFactory
import net.pengcook.android.presentation.core.model.Recipe

@AssistedFactory
interface DetailRecipeViewModelFactory {
    fun create(recipe: Recipe): DetailRecipeViewModel
}
