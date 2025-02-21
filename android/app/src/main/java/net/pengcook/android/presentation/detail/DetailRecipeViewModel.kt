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
import net.pengcook.android.presentation.core.model.RecipeForItem
import net.pengcook.android.presentation.core.model.User
import net.pengcook.android.presentation.core.util.Event

class DetailRecipeViewModel
    @AssistedInject
    constructor(
        @Assisted private val id: Long,
        private val likeRepository: LikeRepository,
        private val feedRepository: FeedRepository,
        private val userControlRepository: UserControlRepository,
    ) : ViewModel(),
        AppbarSingleActionEventListener {
        private val _uiEvent: MutableLiveData<Event<DetailRecipeUiEvent>> = MutableLiveData()
        val uiEvent: LiveData<Event<DetailRecipeUiEvent>>
            get() = _uiEvent

        private val _uiState = MutableLiveData<RecipeDetailUiState>()
        val uiState: LiveData<RecipeDetailUiState>
            get() = _uiState

        init {
            loadRecipe()
        }

        fun loadRecipe() {
            viewModelScope.launch {
                feedRepository.fetchRecipe(id)
                    .onSuccess { recipe ->
                        _uiState.value =
                            RecipeDetailUiState(
                                isLoading = false,
                                isSuccessful = true,
                                recipe = recipe,
                                isLike = recipe.isLiked,
                                likeCount = recipe.likeCount,
                            )
                    }.onFailure {
                        _uiEvent.value = Event(DetailRecipeUiEvent.LoadRecipeFailure)
                    }
            }
        }

        fun toggleLike() {
            viewModelScope.launch {
                val currentState = _uiState.value ?: return@launch
                val newIsLike = currentState.isLike?.not() ?: return@launch
                val newLikeCount =
                    if (newIsLike) currentState.likeCount?.plus(1) else currentState.likeCount?.minus(1)

                // 낙관적 업데이트: UI를 즉시 업데이트
                _uiState.value =
                    currentState.copy(
                        isLike = newIsLike,
                        likeCount = newLikeCount,
                    )

                // 서버에 변경사항 전송
                likeRepository.postIsLike(id, newIsLike)
                    .onSuccess { result ->
                    }.onFailure {
                        _uiState.value = currentState
//                            _uiEvent.value = Event(DetailRecipeUiEvent.)
                    }
            }
        }

        fun onNavigateToMakingStep() {
            _uiEvent.value = Event(DetailRecipeUiEvent.NavigateToStep)
        }

        fun onNavigateToComment() {
            _uiEvent.value = Event(DetailRecipeUiEvent.NavigateToComment)
        }

        fun onNavigateToProfile() {
            val recipe = uiState.value?.recipe ?: return
            _uiEvent.value = Event(DetailRecipeUiEvent.NavigateToProfile(recipe))
        }

        fun deleteRecipe() {
            viewModelScope.launch {
                feedRepository
                    .deleteRecipe(id)
                    .onSuccess {
                        _uiEvent.value = Event(DetailRecipeUiEvent.NavigateBackWithDelete("delete"))
                    }.onFailure { throwable ->
                        _uiEvent.value = Event(DetailRecipeUiEvent.DeletionError)
                    }
            }
        }

        fun blockUser() {
            viewModelScope.launch {
                val user = uiState.value?.recipe?.user
                userControlRepository.blockUser(user?.id ?: return@launch)
                _uiEvent.value = Event(DetailRecipeUiEvent.NavigateBackWithBlock(user.username))
            }
        }

        fun openMenu(recipe: RecipeForItem) {
            _uiEvent.value = Event(DetailRecipeUiEvent.OpenMenu(recipe))
        }

        override fun onNavigateBack() {
            _uiEvent.value = Event(DetailRecipeUiEvent.NavigateBack)
        }

        companion object {
            fun provideFactory(
                assistedFactory: DetailRecipeViewModelFactory,
                recipeId: Long,
            ): ViewModelProvider.Factory =
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return assistedFactory.create(recipeId) as T
                    }
                }
        }
    }

data class RecipeDetailUiState(
    val isLoading: Boolean = true,
    val isSuccessful: Boolean = false,
    val recipe: RecipeForItem =
        RecipeForItem(
            recipeId = 0,
            title = "",
            category = emptyList(),
            thumbnail = "",
            user =
                User(
                    id = 0,
                    username = "",
                    profile = "",
                ),
            isLiked = false,
            likeCount = 0,
            commentCount = 0,
            difficulty = 0,
            cookingTime = "00:00:00",
            ingredients = emptyList(),
            introduction = "",
            mine = false,
        ),
    val isLike: Boolean? = null,
    val likeCount: Long? = null,
)
