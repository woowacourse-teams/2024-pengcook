package net.pengcook.android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.feed.FeedRepository

class HomeViewModelFactory(
    private val feedRepository: FeedRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(feedRepository) as T
        }
        throw IllegalArgumentException()
    }
}
