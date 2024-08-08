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
    val navigateToStepEvent: LiveData<Event<Boolean>>
        get() = _navigateToStepEvent

    private val _navigateToCommentEvent = MutableLiveData<Event<Boolean>>()
    val navigateToCommentEvent: LiveData<Event<Boolean>>
        get() = _navigateToCommentEvent

    private val _isLike = MutableLiveData<Boolean>()
    val isLike: LiveData<Boolean> get() = _isLike

    private val _error = MutableLiveData<Event<Unit>>()
    val error: LiveData<Event<Unit>> get() = _error

    private val _likeCount = MutableLiveData(recipe.likeCount)
    val likeCount: LiveData<Long> get() = _likeCount

    init {
        loadLikeData()
    }

    fun toggleLike() {
        _isLike.value = _isLike.value?.not()
        if (_isLike.value == true) {
            _likeCount.value = _likeCount.value?.plus(1)
        } else {
            _likeCount.value = _likeCount.value?.minus(1)
        }
        postLike()
    }

    fun onNavigateToMakingStep() {
        _navigateToStepEvent.value = Event(true)
    }

    fun onNavigateToComment() {
        _navigateToCommentEvent.value = Event(true)
    }

    private fun loadLikeData() {
        viewModelScope.launch {
            likeRepository
                .loadIsLike(recipeId = recipe.recipeId)
                .onSuccess { isLike ->
                    _isLike.value = isLike
                }.onFailure { _ ->
                    _error.value = Event(Unit)
                }
        }
    }

    private fun postLike() {
        viewModelScope.launch {
            isLike.value?.let {
                likeRepository.postIsLike(recipe.recipeId, it).onFailure { _ ->
                    _error.value = Event(Unit)
                }
            }
        }
    }
}
