package net.pengcook.android.presentation.home

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
import net.pengcook.android.data.datasource.FeedPagingSource
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.home.listener.FeedItemEventListener
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val feedRepository: FeedRepository,
    ) : ViewModel(),
        FeedItemEventListener {
        private val _uiEvent: MutableLiveData<Event<HomeEvent>> = MutableLiveData()
        val uiEvent: LiveData<Event<HomeEvent>>
            get() = _uiEvent

        private val _feedData = MutableLiveData<PagingData<Recipe>>()
        val feedData: LiveData<PagingData<Recipe>> = _feedData

        init {
            loadFeedData()
        }

        private fun loadFeedData() {
            val pager =
                Pager(
                    config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
                    pagingSourceFactory = { FeedPagingSource(feedRepository) },
                )

            viewModelScope.launch {
                pager.flow
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _feedData.value = pagingData
                    }
            }
        }

        fun refreshFeed() {
            loadFeedData()
        }

        override fun onNavigateToDetail(recipe: Recipe) {
            _uiEvent.value = Event(HomeEvent.NavigateToDetail(recipe))
        }

        companion object {
            private const val PAGE_SIZE = 10
        }
    }

sealed interface HomeEvent {
    data class NavigateToDetail(
        val recipe: Recipe,
    ) : HomeEvent
}
