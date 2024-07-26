package net.pengcook.android.presentation.category.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import net.pengcook.android.data.datasource.CategoryFeedPagingSource
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.core.listener.AppbarActionEventListener
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.home.listener.FeedItemEventListener

class CategoryFeedListViewModel(
    private val feedRepository: FeedRepository,
    private val category: String,
) : ViewModel(), AppbarActionEventListener, FeedItemEventListener {
    private val _uiEvent: MutableLiveData<Event<CategoryFeedListUiEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<CategoryFeedListUiEvent>>
        get() = _uiEvent

    val feedData: LiveData<PagingData<Recipe>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                CategoryFeedPagingSource(
                    category = category,
                    fetchFeeds = feedRepository::fetchRecipesByCategory,
                )
            },
        )
            .liveData
            .cachedIn(viewModelScope)

    override fun onNavigateBack() {
        _uiEvent.value = Event(CategoryFeedListUiEvent.NavigateBack)
    }

    override fun onNavigateToDetail(recipe: Recipe) {
        _uiEvent.value = Event(CategoryFeedListUiEvent.NavigateToDetail(recipe))
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
