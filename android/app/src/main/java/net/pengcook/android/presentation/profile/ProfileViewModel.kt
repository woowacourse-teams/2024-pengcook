package net.pengcook.android.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.core.util.Event
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val profilePagingSourceFactory: ProfilePagingSourceFactory,
    ) : ViewModel(), DoubleButtonClickListener, ProfileFeedClickListener {
        private val _uiEvent: MutableLiveData<Event<ProfileUiEvent>> = MutableLiveData()
        val uiEvent: LiveData<Event<ProfileUiEvent>>
            get() = _uiEvent

        val items: LiveData<PagingData<ProfileViewItem>> =
            Pager(
                config = PagingConfig(pageSize = 30, initialLoadSize = 30),
                pagingSourceFactory = { profilePagingSource },
            ).liveData.cachedIn(viewModelScope)

        override fun onLeftButtonClick() {
            _uiEvent.value = Event(ProfileUiEvent.NavigateToEditProfile)
        }

        override fun onRightButtonClick() {
            _uiEvent.value = Event(ProfileUiEvent.NavigateToSetting)
        }

        override fun onClick(recipe: RecipeForList) {
            _uiEvent.value = Event(ProfileUiEvent.NavigateToRecipeDetail(recipe))
        }

        private val profilePagingSource: ProfilePagingSource by lazy {
            profilePagingSourceFactory.create(
                initialPageNumber = 0,
                profileFeedType = ProfileFeedType.MyFeed,
            )
        }
    }

sealed interface ProfileUiEvent {
    data object NavigateToEditProfile : ProfileUiEvent

    data object NavigateToSetting : ProfileUiEvent

    data class NavigateToRecipeDetail(val recipe: RecipeForList) : ProfileUiEvent
}
