package net.pengcook.android.presentation.making.step

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository

class StepMakingViewModelFactory(
    private val recipeId: Long,
    private val maximumStep: Int = 15,
    private val recipeStepMakingRepository: RecipeStepMakingRepository,
    private val makingRecipeRepository: MakingRecipeRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepMakingViewModel::class.java)) {
            return StepMakingViewModel(
                recipeId = recipeId,
                maximumStep = maximumStep,
                recipeStepMakingRepository = recipeStepMakingRepository,
                makingRecipeRepository = makingRecipeRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
