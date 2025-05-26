package net.pengcook.android.presentation.category.list

import dagger.assisted.AssistedFactory

@AssistedFactory
interface CategoryFeedListViewModelFactory {
    fun create(category: String): CategoryFeedListViewModel
}
