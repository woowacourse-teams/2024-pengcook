package net.pengcook.android.presentation.category.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.pengcook.android.data.datasource.FeedPagingSource
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.home.listener.FeedItemEventListener

class CategoryFeedListViewModel @AssistedInject constructor(
    private val feedRepository: FeedRepository,
    @Assisted private val category: String,
) : ViewModel(),
    AppbarSingleActionEventListener,
    FeedItemEventListener {
    private val _uiEvent: MutableLiveData<Event<CategoryFeedListUiEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<CategoryFeedListUiEvent>>
        get() = _uiEvent

    val feedData: LiveData<PagingData<Recipe>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                FeedPagingSource(
                    feedRepository = feedRepository,
                    category = category,
                )
            },
        ).liveData
            .cachedIn(viewModelScope)

    override fun onNavigateBack() {
        _uiEvent.value = Event(CategoryFeedListUiEvent.NavigateBack)
    }

    override fun onNavigateToDetail(recipe: Recipe) {
        _uiEvent.value = Event(CategoryFeedListUiEvent.NavigateToDetail(recipe))
    }

    companion object {
        private const val PAGE_SIZE = 10

        fun provideFactory(
            assistedFactory: CategoryFeedListViewModelFactory,
            category: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CategoryFeedListViewModel::class.java)) {
                    return assistedFactory.create(category) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

}
