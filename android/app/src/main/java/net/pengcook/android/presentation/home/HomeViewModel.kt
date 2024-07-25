package net.pengcook.android.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import net.pengcook.android.data.datasource.FeedPagingSource
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.home.listener.FeedItemEventListener

class HomeViewModel(
    private val feedRepository: FeedRepository,
) : ViewModel(), FeedItemEventListener {
    val feedData: LiveData<PagingData<Recipe>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { FeedPagingSource(fetchFeeds = feedRepository::fetchRecipes) },
        )
            .liveData
            .cachedIn(viewModelScope)

    companion object {
        private const val PAGE_SIZE = 10
    }

    override fun onNavigateToDetail(recipe: Recipe) {
        // Navigate to detail page
    }
}
