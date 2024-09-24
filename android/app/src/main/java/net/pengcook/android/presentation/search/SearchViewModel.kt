package net.pengcook.android.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import net.pengcook.android.data.datasource.FeedPagingSource
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.home.listener.FeedItemEventListener
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    feedRepository: FeedRepository,
) : ViewModel(), SearchEventListener, FeedItemEventListener {
    val searchKeyword: MutableStateFlow<String> = MutableStateFlow(INITIAL_KEYWORD)

    private val _uiEvent: MutableLiveData<Event<SearchUiEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<SearchUiEvent>>
        get() = _uiEvent

    private val searchPagingSource: FeedPagingSource =
        FeedPagingSource(
            feedRepository = feedRepository,
        )

    val allRecipes: Flow<PagingData<Recipe>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE),
            pagingSourceFactory = { searchPagingSource },
        )
            .flow
            .cachedIn(viewModelScope)
            .combine(searchKeyword) { pagingData, keyword ->
                pagingData.filter { recipe -> hasKeyword(keyword, recipe) }
            }

    override fun onSearchError() {
        _uiEvent.value = Event(SearchUiEvent.SearchFailure)
    }

    override fun onNavigateToDetail(recipe: Recipe) {
        _uiEvent.value = Event(SearchUiEvent.RecipeSelected(recipe))
    }

    private fun hasKeyword(
        keyword: String,
        recipe: Recipe,
    ): Boolean =
        keyword.isEmpty() ||
                recipe.title.contains(keyword, ignoreCase = true) ||
                recipe.introduction.contains(keyword, ignoreCase = true)

    companion object {
        private const val INITIAL_KEYWORD = ""
        private const val PAGE_SIZE = 20
    }
}
