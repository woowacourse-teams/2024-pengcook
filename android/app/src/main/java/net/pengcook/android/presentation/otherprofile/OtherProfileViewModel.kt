package net.pengcook.android.presentation.otherprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.profile.ProfileRepository
import net.pengcook.android.data.repository.usercontrol.UserControlRepository
import net.pengcook.android.presentation.profile.ProfilePagingSourceFactory

class OtherProfileViewModel
    @AssistedInject
    constructor(
        private val profilePagingSourceFactory: ProfilePagingSourceFactory,
        private val profileRepository: ProfileRepository,
        private val userControlRepository: UserControlRepository,
        @Assisted private val userId: Long,
    ) : ViewModel() {
        private var _state = MutableStateFlow(OtherProfileState())
        val state: StateFlow<OtherProfileState> = _state
            .onStart {
                fetchProfile()
                fetchRecipes()
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                _state.value,
            )

        private fun fetchProfile() {
            viewModelScope.launch {
                val profile = profileRepository.fetchUserInformation(userId).getOrThrow()
                _state.value = _state.value.copy(userProfile = profile)
                _state.value = _state.value.copy(isFollowing = profile.isFollow)
            }
        }

        private fun fetchRecipes() {
            viewModelScope.launch {
                val items = profileRepository.fetchUserFeeds(userId, 0, 30).getOrThrow()
                _state.value = _state.value.copy(recipes = items)
            }
        }

        fun onAction(action: OtherProfileAction) {
            when (action) {
                is OtherProfileAction.OnFollowClick -> {
                    viewModelScope.launch {
                        userControlRepository.followUser(userId).onSuccess {
                            _state.value = state.value.copy(isFollowing = true)
                        }
                    }
                }

                is OtherProfileAction.OnUnfollowClick -> {
                    viewModelScope.launch {
                        println("viewmodel: follow")
                        userControlRepository.unfollowUser(userId).onSuccess {
                            _state.value = state.value.copy(isFollowing = false)
                        }
                    }
                }

                is OtherProfileAction.OnBackClick -> {
                    // navigateBack()
                }

                else -> Unit
            }
        }

        companion object {
            fun provideFactory(
                assistedFactory: OtherProfileViewModelFactory,
                userId: Long,
            ): ViewModelProvider.Factory =
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return assistedFactory.create(userId) as T
                    }
                }
        }
    }
