package net.pengcook.android.presentation.category.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.feed.FeedRepository

class CategoryFeedListViewModelFactory(
    private val feedRepository: FeedRepository,
    private val category: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryFeedListViewModel::class.java)) {
            return CategoryFeedListViewModel(
                feedRepository = feedRepository,
                category = category,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
