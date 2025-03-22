package net.pengcook.android.presentation.follow2

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.profile.ProfileRepository
import net.pengcook.android.data.repository.usercontrol.UserControlRepository
import net.pengcook.android.presentation.follow2.model.toPresentationModel

class FollowListViewModel
    @AssistedInject
    constructor(
        private val profileRepository: ProfileRepository,
        private val authorizationRepository: AuthorizationRepository,
        private val userControlRepository: UserControlRepository,
        @Assisted private val profileOwnerId: Long,
    ) : ViewModel() {
        private val _state = MutableStateFlow(FollowListState())
        val state: StateFlow<FollowListState> = _state
            .onStart {
                checkIsMyUserId()
                fetchFollowerInfo()
                fetchFollowingInfo()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                _state.value,
            )

        fun onAction(action: FollowListAction) {
            when (action) {
                is FollowListAction.OnTabSelected -> {
                    _state.update {
                        it.copy(
                            selectedTabIndex = action.index,
                        )
                    }
                }

                FollowListAction.NavigateBack -> {}
                is FollowListAction.OnRemoveFollower -> TODO()
                is FollowListAction.OnUnfollow -> {
                    val selectedUserId = action.userId
                    unFollowUser(userId = selectedUserId)
                }

                FollowListAction.HideDialog -> {
                    _state.update {
                        it.copy(
                            showDialog = false,
                            selectedFollowInfo = null,
                        )
                    }
                }

                is FollowListAction.ShowDialog -> {
                    _state.update {
                        it.copy(
                            showDialog = true,
                            selectedFollowInfo = it.followings.find { followInfo ->
                                followInfo.userId == action.userId
                            },
                        )
                    }
                }
            }
        }

        private fun fetchFollowerInfo() {
            viewModelScope.launch {
                userControlRepository.fetchFollowers(profileOwnerId).onSuccess {
                    _state.value = _state.value.copy(
                        followers = it.follows.map { followUser ->
                            followUser.toPresentationModel()
                        },
                        followerCount = it.followCount,
                    )
                }
                _state.update {
                    it.copy(username = fetchUsername())
                }
            }
        }

        private fun fetchFollowingInfo() {
            viewModelScope.launch {
                userControlRepository.fetchFollowings(profileOwnerId).onSuccess {
                    _state.value = _state.value.copy(
                        followings = it.follows.map { followUser ->
                            followUser.toPresentationModel()
                        },
                        followingCount = it.followCount,
                    )
                }
            }
        }

        private suspend fun fetchUsername(): String {
            return profileRepository.fetchUserInformation(profileOwnerId).getOrThrow().username
        }

        private fun unFollowUser(userId: Long) {
            viewModelScope.launch {
                userControlRepository.unfollowUser(userId).onSuccess {
                    _state.update {
                        it.copy(
                            followings = it.followings.filter { followInfo ->
                                followInfo.userId != userId
                            },
                            followingCount = it.followingCount - 1,
                            showDialog = false,
                            selectedFollowInfo = null,
                        )
                    }
                }
            }
        }

        private fun checkIsMyUserId() {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isMine = isMyUserId(),
                    )
                }
            }
        }

        private suspend fun isMyUserId(): Boolean {
            return authorizationRepository.fetchUserInformation().getOrThrow().id == profileOwnerId
        }

        companion object {
            fun provideFactory(
                assistedFactory: FollowListViewModelFactory,
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
