package net.pengcook.android.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.data.repository.like.LikeRepository
import net.pengcook.android.data.repository.usercontrol.UserControlRepository
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.util.Event

class DetailRecipeViewModel @AssistedInject constructor(
    @Assisted private val recipe: Recipe,
    private val likeRepository: LikeRepository,
    private val feedRepository: FeedRepository,
    private val userControlRepository: UserControlRepository,
) : ViewModel(),
    AppbarSingleActionEventListener {
    private val _uiState: MutableLiveData<Event<DetailRecipeUiEvent>> = MutableLiveData()
    val uiState: LiveData<Event<DetailRecipeUiEvent>>
        get() = _uiState

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
        _uiState.value = Event(DetailRecipeUiEvent.NavigateToStep)
    }

    fun onNavigateToComment() {
        _uiState.value = Event(DetailRecipeUiEvent.NavigateToComment)
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

    fun deleteRecipe() {
        viewModelScope.launch {
            feedRepository
                .deleteRecipe(recipe.recipeId)
                .onSuccess {
                    _uiState.value = Event(DetailRecipeUiEvent.NavigateBackWithDelete("delete"))
                }.onFailure { throwable ->
                    _error.value = Event(Unit)
                }
        }
    }

    fun blockUser() {
        viewModelScope.launch {
            userControlRepository.blockUser(recipe.user.id)
        }
        _uiState.value = Event(DetailRecipeUiEvent.NavigateBackWithBlock(recipe.user.username))
    }

    override fun onNavigateBack() {
        _uiState.value = Event(DetailRecipeUiEvent.NavigateBack)
    }

    companion object {
        fun provideFactory(
            assistedFactory: DetailRecipeViewModelFactory,
            recipe: Recipe,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(recipe) as T
            }
        }
    }
}
