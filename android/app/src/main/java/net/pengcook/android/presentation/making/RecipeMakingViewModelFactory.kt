package net.pengcook.android.presentation.making

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository

@Suppress("UNCHECKED_CAST")
class RecipeMakingViewModelFactory(private val makingRecipeRepository: MakingRecipeRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeMakingViewModel::class.java)) {
            return RecipeMakingViewModel(
                makingRecipeRepository = makingRecipeRepository,
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
