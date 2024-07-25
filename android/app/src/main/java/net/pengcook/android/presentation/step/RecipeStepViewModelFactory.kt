package net.pengcook.android.presentation.step

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.feed.FeedRepository

class RecipeStepViewModelFactory(
    private val recipeId: Long,
    private val feedRepository: FeedRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeStepViewModel::class.java)) {
            return RecipeStepViewModel(
                recipeId = recipeId,
                feedRepository = feedRepository,
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
