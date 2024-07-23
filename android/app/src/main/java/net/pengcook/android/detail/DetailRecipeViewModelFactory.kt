package net.pengcook.android.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.domain.model.Recipe

class DetailRecipeViewModelFactory(
    private val recipe: Recipe,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(DetailRecipeViewModel::class.java)) {
            DetailRecipeViewModel(
                recipe = recipe,
            ) as T
        } else {
            throw IllegalArgumentException()
        }
}
