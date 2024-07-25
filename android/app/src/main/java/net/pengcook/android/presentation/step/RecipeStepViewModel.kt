package net.pengcook.android.presentation.step

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.core.model.RecipeStep

class RecipeStepViewModel(
    private val recipeId: Long,
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private var _recipeSteps: MutableLiveData<List<RecipeStep>> = MutableLiveData(emptyList())
    val recipeSteps: LiveData<List<RecipeStep>>
        get() = _recipeSteps

    fun fetchRecipeSteps() {
        viewModelScope.launch {
            val response = feedRepository.fetchRecipeSteps(recipeId)
            if (response.isSuccess) {
                _recipeSteps.value = response.getOrNull() ?: emptyList()
            }
        }
    }
}
