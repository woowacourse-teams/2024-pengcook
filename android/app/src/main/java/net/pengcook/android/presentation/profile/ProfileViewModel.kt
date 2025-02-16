package net.pengcook.android.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.core.util.Event
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val profilePagingSourceFactory: ProfilePagingSourceFactory,
    ) : ViewModel(),
        ProfileButtonClickListener,
        ProfileFeedClickListener {
        private val _uiEvent: MutableLiveData<Event<ProfileUiEvent>> = MutableLiveData()
        val uiEvent: LiveData<Event<ProfileUiEvent>>
            get() = _uiEvent

        private val _items: MutableLiveData<PagingData<ProfileViewItem>> = MutableLiveData()
        val items: LiveData<PagingData<ProfileViewItem>>
            get() = _items

        init {
            loadData()
        }

        fun loadData() {
            println("loadData")
            val pager =
                Pager(
                    config = PagingConfig(pageSize = 30, initialLoadSize = 30),
                    pagingSourceFactory = {
                        profilePagingSourceFactory.create(
                            initialPageNumber = 0,
                            profileFeedType = ProfileFeedType.MyFeed,
                        )
                    },
                )

            viewModelScope.launch {
                pager.flow
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        println("collected")
                        _items.value = pagingData
                    }
            }
        }

        override fun onClick(recipe: RecipeForList) {
            _uiEvent.value = Event(ProfileUiEvent.NavigateToRecipeDetail(recipe))
        }

        override fun onProfileEditBtnClick() {
            _uiEvent.value = Event(ProfileUiEvent.NavigateToEditProfile)
        }

        override fun onSettingBtnClick() {
            _uiEvent.value = Event(ProfileUiEvent.NavigateToSetting)
        }

        override fun onFollowListBtnClick() {
            _uiEvent.value = Event(ProfileUiEvent.NavigateToFollowList)
        }

        override fun onCommentListBtnClick() {
            _uiEvent.value = Event(ProfileUiEvent.NavigateToCommentList)
        }

//        private val profilePagingSource: ProfilePagingSource by lazy {
//            profilePagingSourceFactory.create(
//                initialPageNumber = 0,
//                profileFeedType = ProfileFeedType.MyFeed,
//            )
//        }
    }

sealed interface ProfileUiEvent {
    data object NavigateToEditProfile : ProfileUiEvent

    data object NavigateToSetting : ProfileUiEvent

    data class NavigateToRecipeDetail(
        val recipe: RecipeForList,
    ) : ProfileUiEvent

    data object NavigateToFollowList : ProfileUiEvent

    data object NavigateToCommentList : ProfileUiEvent
}
