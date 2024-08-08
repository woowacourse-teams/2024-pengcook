package net.pengcook.android.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.like.LikeRepository
import net.pengcook.android.presentation.core.model.Recipe

class DetailRecipeViewModelFactory(
    private val recipe: Recipe,
    private val likeRepository: LikeRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(DetailRecipeViewModel::class.java)) {
            DetailRecipeViewModel(
                recipe = recipe,
                likeRepository = likeRepository,
            ) as T
        } else {
            throw IllegalArgumentException()
        }
}
