package net.pengcook.android.presentation.making.step

import dagger.assisted.AssistedFactory

@AssistedFactory
interface StepMakingViewModelFactory {
    fun create(recipeId: Long, maximumStep: Int): StepMakingViewModel
}
