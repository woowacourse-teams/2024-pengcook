package net.pengcook.android.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.like.LikeRepository
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.util.Event

class DetailRecipeViewModel(
    private val recipe: Recipe,
    private val likeRepository: LikeRepository,
) : ViewModel() {
    private val _navigateToStepEvent = MutableLiveData<Event<Boolean>>()
    val navigateToStepEvent: LiveData<Event<Boolean>> get() = _navigateToStepEvent

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val _likeCount = MutableLiveData<Int>()
    val likeCount: LiveData<Int> get() = _likeCount

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        loadLikeData()
    }

    fun toggleFavorite() {
        _isFavorite.value = _isFavorite.value?.not()
        postLike()
    }

    fun onNavigateToMakingStep() {
        _navigateToStepEvent.value = Event(true)
    }

    private fun loadLikeData() {
        viewModelScope.launch {
            likeRepository.getLikeCount(recipe.recipeId).onSuccess { count ->
                _likeCount.value = count
                _isFavorite.value = count > 0
            }.onFailure { throwable ->
                _error.value = throwable.message
            }
        }
    }

    private fun postLike() {
        viewModelScope.launch {
            likeRepository.postLike(recipe.recipeId).onFailure { throwable ->
                _error.value = throwable.message
            }
        }
    }
}
