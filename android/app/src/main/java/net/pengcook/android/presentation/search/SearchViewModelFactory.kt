package net.pengcook.android.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.feed.FeedRepository

class SearchViewModelFactory(
    private val feedRepository: FeedRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(feedRepository) as T
        }
        throw IllegalArgumentException()
    }
}
