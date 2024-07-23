package net.pengcook.android.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import net.pengcook.android.data.FeedPagingSource
import net.pengcook.android.data.repository.DummyFeedsRepository
import net.pengcook.android.listner.FeedItemEventListener
import net.pengcook.android.model.Feed

class HomeViewModel : ViewModel(), FeedItemEventListener {
    private val dummyFeedsRepository = DummyFeedsRepository()
    val feedData: LiveData<PagingData<Feed>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { FeedPagingSource(fetchFeeds = dummyFeedsRepository::fetchFeeds) },
        )
            .liveData
            .cachedIn(viewModelScope)

    companion object {
        private const val PAGE_SIZE = 3
    }

    override fun onNavigateToDetail(feedInfo: Feed) {
        // Navigate to detail page
    }

}
